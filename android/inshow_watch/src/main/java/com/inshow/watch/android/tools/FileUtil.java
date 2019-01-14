package com.inshow.watch.android.tools;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * Created by chendong on 2017/5/24.
 */
public class FileUtil {

    public static String ReadFile(Context context, int rawres) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().openRawResource(rawres));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *采用服务端配置文件
     * @param context
     * @param filepath
     * @return
     */
    public static String ReadFile(Context context,String filepath) {
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String s;
            while((s = br.readLine())!=null){
                result.append(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 下载远程文件并保存到本地
     */
    public static void downloadFile(String urlStr, String file) throws IOException
    {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    /**
     *世界城市json文件
     * @return
     */
    public static final String getCityFilePath() {
        String sd_path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "inshow";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return sd_path+File.separator +"city.json";
    }

    /**
     *节假日json文件
     * @return
     */
    public static final String getFestivaFilePath() {
        String sd_path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "inshow";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return sd_path+File.separator +"festival.json";
    }

    /**
     * 调试的log
     * @return
     */
    public static final String getLogFilePath() {
        String sd_path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "inshow";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return sd_path+File.separator +"log.txt";
    }

    /**
     * 调试的log
     * @return
     */
    public static final String getGsensorFilePath() {
        String sd_path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "inshow";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return sd_path+File.separator +"GSensor.txt";
    }

    /**
     * 写配置文件
     * @param str
     * @param file
     * @throws IOException
     */
    public  static void writeDataToFile(String str,String file) throws IOException {
        File f = new File(file);
        //每次写之前删除文件
        if(f.exists()){
            f.delete();
        }
        f.createNewFile();
        if(!TextUtils.isEmpty(str)) {
            byte bytes[] = str.getBytes();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();
        }
    }

    /**
     * 写Log文件
     * @param conent
     * @param file
     * @throws IOException
     */
    public  static void writeLogDataToFile(String conent,String file) throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(TimeUtil.getNowTimeString()+"\t\t\t\t"+conent+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * writeGsensorFile
     * @param conent
     * @param file
     * @throws IOException
     */
    public  static void writeGsensorFile(String conent,String file) throws IOException {
        BufferedWriter out = null;
        File f = new File(file);
        f.delete();
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件夹
     */
    public static void deleteDir() {
        File sdFile = Environment.getExternalStorageDirectory();
        String sd_path = sdFile.getPath() + File.separator + "inshow";
        File dir = new File(sd_path);
        if ( !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}