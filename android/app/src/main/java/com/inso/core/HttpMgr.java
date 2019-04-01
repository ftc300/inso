package com.inso.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inso.plugin.tools.L;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/11/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class HttpMgr {


    static RequestQueue mQueue = null;

    static StringRequest stringRequest = null;


    public static RequestQueue getRequestQueue(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
        return mQueue;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Map<String, String> obj2Map(Object obj) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object o = fields[i].get(obj);
                if (o != null)
                    map.put(varName, o.toString());
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }


    public static void postStringRequest(Context context, final String url, final Object obj, final IResponse<String> iResponse) {
        HttpMgr.getRequestQueue(context.getApplicationContext()).add(HttpMgr.postString(context, url, obj, iResponse, true));
    }

    public static void postStringRequestWithoutToken(Context context, final String url, final Object obj, final IResponse<String> iResponse) {
        HttpMgr.getRequestQueue(context.getApplicationContext()).add(HttpMgr.postString(context, url, obj, iResponse, false));
    }

    public static void getJsonObjectRequest(Context context, final String url, final IResponse<JSONObject> iResponse) {
        HttpMgr.getRequestQueue(context.getApplicationContext()).add(HttpMgr.getRequest(context, url, iResponse));
    }


    public static void postUploadImageRequest( String requestURL,File file, final IResponse<String> iResponse) {
        L.d("upload image request url:" + requestURL);
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10 * 10000000);
            conn.setConnectTimeout(10 * 10000000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                    + BOUNDARY);
            if (file != null) {
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"avatar\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=utf-8 " + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        stringBuilder.append(output);
                    }
                    iResponse.onSuccess(stringBuilder.toString());
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        iResponse.onFail();
    }

    /**
     * 2.获取StringRequest对象
     *
     * @param url 请求的url
     * @return StringRequest
     */
    private static StringRequest postString(final Context context, final String url, final Object obj, final IResponse<String> iResponse, final boolean withToken) {
        final Map<String, String> map = obj2Map(obj);
        L.d("######### post data ######### ", map.toString());
        return stringRequest = new StringRequest(Request.Method.POST, getTokenUrl(context, url, withToken),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        L.d("######### response from " + getTokenUrl(context, url, withToken) + "######### \n " + response.toString());
                        iResponse.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.e("######### response error from " + url + "######### \n " + error.toString());
                iResponse.onFail();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
    }

    /**
     * 获取JsonObjectRequest对象
     *
     * @param url 请求url
     * @return JsonObjectRequest
     */
    private static JsonObjectRequest getRequest(final Context context, final String url, final IResponse<JSONObject> iResponse) {
        return new JsonObjectRequest(getTokenUrl(context, url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        L.d("JsonObjectRequest", response.toString());
                        L.d("######### response from " + url + "######### \n " + response.toString());
                        iResponse.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                L.e("JsonObjectRequest", "onErrorResponse: " + volleyError.getMessage());
                iResponse.onFail();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

        };

    }


    private static JsonObjectRequest postRequest(final String url, String content, final IResponse<JSONObject> iResponse) {
        L.d("######### post data ########" + content);
        return new JsonObjectRequest(Request.Method.POST, url, content,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        L.d("######### response from " + url + "######### \n " + response.toString());
                        iResponse.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.e("######### response from " + url + "######### \n " + error.toString());
                iResponse.onFail();
            }
        }) {
//          @Override
//          public Map<String, String> getHeaders() {
//              HashMap<String, String> headers = new HashMap<String, String>();
//              headers.put("Accept", "*/*");
//              headers.put("Accept-Encoding","gzip,deflate");
//              headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//              return headers;
//          }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("platform","xiaomi");
//                map.put("unionId","TGIsaVC_10gI9yQwwn3swNz8IHMKiJ8m8O5qASSA");
//                return map;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded;charset=UTF-8";
//            }

        };
    }


    private static String getTokenUrl(Context context, String url) {
        return url + "?access_token=" + UserMgr.getAccessToken(context);
    }

    private static String getTokenUrl(Context context, String url, boolean hasToken) {
        return hasToken ? getTokenUrl(context, url) : url;
    }

    /**
     * 2.获取StringRequest对象
     *
     * @param url 请求的url
     * @return StringRequest
     */
    public static void testNet(String url, final INet net) {
        new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        net.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                net.onFail();
            }
        });
    }


    interface INet {
        void onSuccess();

        void onFail();
    }

    public interface IResponse<T> {
        void onSuccess(T t);

        void onFail();
    }


}
