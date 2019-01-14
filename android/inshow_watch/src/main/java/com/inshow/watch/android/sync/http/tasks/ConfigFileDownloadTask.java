package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;
import android.os.Looper;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.bean.HttpConfigV;
import com.inshow.watch.android.sync.http.bean.HttpFileRes;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.L;
import com.squareup.picasso.Request;
import com.xiaomi.smarthome.device.api.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.CONFIG_VERSION;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.FESTIVAL_CHINA;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.WORLD_CITY;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;

/**
 * Created by chendong on 2017/5/27.
 */
public class ConfigFileDownloadTask implements Runnable{

    private String MAC ;
    private DBHelper mDBHelper;
    final CountDownLatch latch = new CountDownLatch(1);
    private HttpFileRes fileRes;
    private HttpConfigV configV;
    public ConfigFileDownloadTask(Context context,DBHelper mDBHelper) {
        this.mDBHelper = mDBHelper;
        MAC = mDBHelper.getCache(SP_ARG_MAC);
    }

    @Override
    public void run() {
        HttpSyncHelper.getAppConfig(new RequestParams(MAC, CONFIG_VERSION, 1), new Callback<Object>() {
            @Override
            public void onFailure(int i, String s) {
                L.e("getAppConfig onFailure:" + s);
            }

            @Override
            public void onSuccess(Object result) {
                L.e("getAppConfig onSuccess:" + result.toString());
                fileRes = AppController.getGson().fromJson(result.toString(),HttpFileRes.class);
                if(!TextUtils.isEmpty(fileRes.content)){
                    L.e("getAppConfig onSuccess item.content:" + fileRes.content);
                    configV = AppController.getGson().fromJson(fileRes.content,HttpConfigV.class);
                   if(configV.world_city > mDBHelper.getConfigV(WORLD_CITY)){
                       HttpSyncHelper.getAppConfig(new RequestParams(MAC,WORLD_CITY,configV.world_city), new Callback<Object>() {
                           @Override
                           public void onSuccess(Object result) {
                               try {
                                   HttpFileRes item  = AppController.getGson().fromJson(result.toString(),HttpFileRes.class);
                                   L.e("getAppConfigSuccess world_city fileUrl:" + item.fileUrl);
                                   if(!TextUtils.isEmpty(item.fileUrl)&&item.fileUrl.contains("http")) {
//                                       downloadCityfile(item);
//                                       FileUtil.writeDataToFile( getJsonByInternet(item.fileUrl), FileUtil.getCityFilePath());
                                       mDBHelper.saveCache(WORLD_CITY,getJsonByInternet(item.fileUrl));
                                       L.e("download city file complete!" );
                                   }
                               } catch (JsonSyntaxException e) {
                                   e.printStackTrace();
                                   L.e(e.getMessage());
                               }
                           }
                           @Override
                           public void onFailure(int i, String s) {
                               L.e("getAppConfigError:" + s);
                           }
                       });
                   }
                    L.e("configV.festival_china:"+configV.festival_china+"mDBHelper.getConfigV(FESTIVAL_CHINA)"+mDBHelper.getConfigV(FESTIVAL_CHINA));
                    if(configV.festival_china > mDBHelper.getConfigV(FESTIVAL_CHINA)){
                       HttpSyncHelper.getAppConfig(new RequestParams(MAC,FESTIVAL_CHINA,configV.festival_china), new Callback<Object>() {
                           @Override
                           public void onSuccess(Object result) {
                               try {
                                   HttpFileRes item  = AppController.getGson().fromJson(result.toString(),HttpFileRes.class);
                                   L.e("getAppConfigSuccess FESTIVAL_CHINA fileUrl:" + item.fileUrl);
                                   if(!TextUtils.isEmpty(item.fileUrl)&&item.fileUrl.contains("http")) {
//                                       FileUtil.writeDataToFile(getJsonByInternet(item.fileUrl), FileUtil.getFestivaFilePath());
                                       mDBHelper.saveCache(FESTIVAL_CHINA,getJsonByInternet(item.fileUrl));
                                       L.e("download FESTIVAL_CHINA file complete!" );
                                   }
                               } catch (JsonSyntaxException e) {
                                   e.printStackTrace();
                                   L.e(e.getMessage());
                               }
                           }
                           @Override
                           public void onFailure(int i, String s) {
                               L.e("getAppConfigError:" + s);
                           }
                       });

                   }
                }

            }
        });

    }


    /**
     * 从网络获取json数据,(String byte[})
     * @param path
     * @return
     */
    private   String getJsonByInternet(String path){
        URL url;
        HttpURLConnection urlConnection=null;
        try {
            L.d("start:: fetch config file content form " + path);
            url= new URL(path.trim());
            urlConnection = (HttpURLConnection) url.openConnection();
            if(200 == urlConnection.getResponseCode()){
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                L.d("config file content :" + baos.toString("utf-8"));
                return baos.toString("utf-8");
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }finally {
            L.d("end:: fetch config file content form " + path);
            if(urlConnection!=null) {
                urlConnection.disconnect();
                urlConnection = null;
            }
        }
        return null;
    }




//    private void downloadCityfile(HttpFileRes item) throws IOException {
//        final OkHttpClient mOkHttpClient = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(item.fileUrl)
//                .build();
//        Response response  = mOkHttpClient.newCall(request).execute();
////                                       call.enqueue(new okhttp3.Callback() {
////                                           @Override
////                                           public void onFailure(Call call, IOException e) {
////                                               L.e("getAppConfig onFailure:" + e.getMessage());
////                                           }
////
////                                           @Override
////                                           public void onResponse(Call call, Response response) throws IOException {
//        L.e("getAppConfig onResponse" );
//        String responseStr = response.body().string();
//        FileUtil.writeDataToFile(responseStr, FileUtil.getCityFilePath());
//        mDBHelper.updateConfigV(WORLD_CITY,configV.world_city);
//        L.e("getAppConfig onResponse write complete" );
////                                           }
////                                       });
//    }
}
