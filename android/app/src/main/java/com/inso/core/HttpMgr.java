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
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.inso.core.Constant.SP_ACCESS_TOKEN;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/11/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class HttpMgr {
    /**
     * 声明RequestQueue对象
     */
    static RequestQueue mQueue = null;

    /**
     * 声明StringRequest对象
     */
    static StringRequest stringRequest = null;

    static JsonObjectRequest mJsonObjectRequest = null;

    /**
     * 1、获取RequestQueue对象
     */
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


    /**
     * 2.获取StringRequest对象
     *
     * @param url 请求的url
     * @return StringRequest
     */
    public static StringRequest postString(final String url, final Object obj , final IResponse<String> iResponse) {
        L.d("######### post data ######### \n" , obj2Map(obj).toString());
        return stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        L.d("######### response from " + url + "######### \n " + response.toString());
                        try {
                            iResponse.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                return obj2Map(obj);
            }
        };
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
        void onSuccess(T t) throws JSONException;

        void onFail();
    }

    /**
     * 获取JsonObjectRequest对象
     * @param url 请求url
     * @return JsonObjectRequest
     */
    public static JsonObjectRequest getRequest(final Context context, final String url, final IResponse<JSONObject> iResponse) {
        return  new JsonObjectRequest(url+"?access_token=" + SPManager.get(context,SP_ACCESS_TOKEN,"") , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        L.d("JsonObjectRequest", response.toString());
                        L.d("######### response from " + url + "######### \n " + response.toString());
                        try {
                            iResponse.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                L.e("JsonObjectRequest", "onErrorResponse: " + volleyError.getMessage());
                iResponse.onFail();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

        };

    }


    public static JsonObjectRequest postRequest(final String url, String content, final IResponse<JSONObject> iResponse) {
        L.d("######### post data ########" + content);
        return new JsonObjectRequest(Request.Method.POST, url, content,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        L.d("######### response from " + url + "######### \n " + response.toString());
                        try {
                            iResponse.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
}
