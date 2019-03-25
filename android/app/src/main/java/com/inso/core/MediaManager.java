package com.inso.core;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.inso.R;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.wigets.ToastWidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 媒体类型工具包
 */
public class MediaManager {
    private static Map<String, String> FORMAT_TO_CONTENTTYPE = new HashMap<>();
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    public static final String PHOTO = "photo";

    static {
        // 音频
        FORMAT_TO_CONTENTTYPE.put("mp3", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("mid", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("midi", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("asf", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("wm", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("wma", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("wmd", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("amr", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("wav", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("3gpp", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("mod", AUDIO);
        FORMAT_TO_CONTENTTYPE.put("mpc", AUDIO);

        // 视频
        FORMAT_TO_CONTENTTYPE.put("fla", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("flv", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("wav", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("wmv", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("avi", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("rm", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("rmvb", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("3gp", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("mp4", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("mov", VIDEO);

        // flash
        FORMAT_TO_CONTENTTYPE.put("swf", VIDEO);
        FORMAT_TO_CONTENTTYPE.put("null", VIDEO);

        // 图片
        FORMAT_TO_CONTENTTYPE.put("jpg", PHOTO);
        FORMAT_TO_CONTENTTYPE.put("jpeg", PHOTO);
        FORMAT_TO_CONTENTTYPE.put("png", PHOTO);
        FORMAT_TO_CONTENTTYPE.put("bmp", PHOTO);
        FORMAT_TO_CONTENTTYPE.put("gif", PHOTO);
    }

    public static final int REQUEST_PHOTO_CROP = 0x80;
    public static final int REQUEST_PHOTO_CROP_RESULT = 0x81;


    /**
     * 存放app部分文件的sd卡目录
     */
    public static final String SD_STORAGE_DIR_NAME = "inso";
    public static final String SAVE_PHONE_NAME_TEMP = "inso_camera_";
    /**
     * 裁剪后的图片存放名字
     */
    public static final String SAVE_PHONE_NAME_CROP = "inso_crop_";
    /**
     * 拍照产生的照片，存放uri
     */
    private static Uri cameraPhotoUri;
    /**
     * 裁剪后的图片文件
     */
    public static File cropPhotoFile;

    /**
     * 拍照获取  参数fragment
     */
    public static void getPhotoFromCamera(Fragment fragment) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastWidget.showWarn(fragment.getActivity(), fragment.getResources().getString(R.string.toast_no_sd));
            return;
        }
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File savedir = new File(savePath, SD_STORAGE_DIR_NAME);
        if (!savedir.exists()) {// 不能自动创建目录
            savedir.mkdirs();
        }

        // 拍照完成后，临时存放照片的一个路径 ,拍照产生的photo是不能存放在当前apk的file等文件夹中的
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File out = new File(savedir.getAbsolutePath(), SAVE_PHONE_NAME_TEMP+ timeStamp);
        if (out.exists()) {
            out.delete();
        }
        if (Build.VERSION.SDK_INT < 24) {
            // 从文件中创建uri
            cameraPhotoUri = Uri.fromFile(out);
        } else {
            //兼容android7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath());
            cameraPhotoUri = fragment.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
        fragment.startActivityForResult(intent, REQUEST_PHOTO_CROP);
    }

    /**
     * 从手机相册获取
     */
    public static void getPhotoFromAlbum(Fragment fragment) {
        try {
            int sdkInt = Build.VERSION.SDK_INT;
            if(sdkInt >= 19){
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                fragment.startActivityForResult(Intent.createChooser(pickIntent,fragment.getResources().getString(R.string.choose_phone)), REQUEST_PHOTO_CROP);
            }else{
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                fragment.startActivityForResult(Intent.createChooser(intent,fragment.getResources().getString(R.string.choose_phone)),REQUEST_PHOTO_CROP);
            }
        } catch (Exception e) {
            //有可能没有系统相册，很难遇到
            ToastWidget.showWarn(fragment.getActivity(), fragment.getResources().getString(R.string.toast_no_album));
        }
    }

    /**
     * 图片上传相关类
     *
     * @param handler
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(final Fragment fragment,
                                        final Handler handler, final int requestCode, int resultCode,
                                        final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            // 图片获取异常
            ToastWidget.showWarn(fragment.getActivity(),fragment.getResources().getString(
                R.string.toast_photo_empty));
            return;
        }
        // 如果返回的是裁剪后的结果
        if (requestCode == REQUEST_PHOTO_CROP) {
            Uri cropUri = null;
            if (data == null) {// 拍照返回的数据
                cropUri = cameraPhotoUri;
            } else {// 相册返回的数据
                cropUri = data.getData();
                if (cropUri == null) {
                    ToastWidget.showWarn(fragment.getActivity(), fragment.getResources()
                        .getString(R.string.toast_photo_empty));
                    return;
                }
                if (!isPhotoFormat(cropUri.toString())) {
                    ToastWidget.showWarn(fragment.getActivity(), fragment.getResources()
                        .getString(R.string.toast_un_image));
                    return;
                }
            }
            cropPhoto(cropUri, fragment);
        } else {// 裁剪结果
            FileInputStream fis = null;
            Bitmap bm = null;
            try {
                fis = new FileInputStream(cropPhotoFile);
                L.d(cropPhotoFile+"路径");
                bm = BitmapFactory.decodeStream(fis);
                Message msg = handler.obtainMessage(Constant.MSG_SHOW_PHOTO,cropPhotoFile.getAbsolutePath());
                handler.sendMessage(msg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally{
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    /**
     * 判断是否img类型
     *
     * @param uri
     * @return
     */
    public static boolean isPhotoFormat(String uri) {
        boolean flag = true;
        // 包含file说明是文件-->图片这条路， 而说不是图库
        if (uri.contains("file")) {
            String attFormat = uri.substring(uri.lastIndexOf('.') + 1);
            flag = PHOTO.equals(getContentType(attFormat));
        }
        return flag;
    }

    /**
     * 拍照或者相册中选择的图片有可能恒很大。给用户一个选择，来裁剪选择图片的一部分做为头像
     *
     * @param uri
     */

    public static void cropPhoto(Uri uri, Fragment fragment) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 发送裁剪信号
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);// X方向上的比例
        intent.putExtra("aspectY", 1);// Y方向上的比例

        int wh = Utils.dpToPx(fragment.getActivity(), (int) fragment.getResources().getDimension(R.dimen.up_head_size));
        // outputX outputY 是裁剪图片的 宽高
        intent.putExtra("outputX", wh);
        intent.putExtra("outputY", wh);

        // 是否保留比例
        // intent.putExtra("scale", true);

        // 是否将数据保留在Bitmap中返回
        // 放入data中;如果为true,用intent.getExtras().getParcelable("data")获取
        // 这种方法不推荐，如果通过bitmap传递，裁剪出来的bitmap或传递过程中或消耗过多的内存，极有可能崩溃
        intent.putExtra("return-data", false);


        getCropPhotoFile();


        // 返回uri中，将数据传回
        Uri cropPhotoUri = Uri.fromFile(cropPhotoFile);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropPhotoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 当circleCrop设置不为null的时候,会裁剪为圆形
        intent.putExtra("circleCrop", true);

        // 裁剪后。到结果的处理了。
        fragment.startActivityForResult(intent, REQUEST_PHOTO_CROP_RESULT);
    }


    // ---------------------------------------------
    /**
     * 拍照或者相册中选择的图片有可能恒很大。给用户一个选择，来裁剪选择图片的一部分做为头像
     *
     * @param uri
     */

    public static void cropPhoto(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 发送裁剪信号
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);// X方向上的比例
        intent.putExtra("aspectY", 1);// Y方向上的比例

        int wh = Utils.dpToPx(activity, (int) activity.getResources().getDimension(R.dimen.up_head_size));
        // outputX outputY 是裁剪图片的 宽高
        intent.putExtra("outputX", wh);
        intent.putExtra("outputY", wh);

        // 是否保留比例
        // intent.putExtra("scale", true);

        // 是否将数据保留在Bitmap中返回
        // 放入data中;如果为true,用intent.getExtras().getParcelable("data")获取
        // 这种方法不推荐，如果通过bitmap传递，裁剪出来的bitmap或传递过程中或消耗过多的内存，极有可能崩溃
        intent.putExtra("return-data", false);


        getCropPhotoFile();


        // 返回uri中，将数据传回
        Uri cropPhotoUri = Uri.fromFile(cropPhotoFile);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropPhotoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 当circleCrop设置不为null的时候,会裁剪为圆形
        intent.putExtra("circleCrop", true);

        // 裁剪后。到结果的处理了。
        activity.startActivityForResult(intent, REQUEST_PHOTO_CROP_RESULT);
    }

    private static void getCropPhotoFile() {

        if (Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED)) {
            String savePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
            File savedir = new File(savePath, SD_STORAGE_DIR_NAME);
            if (!savedir.exists()) {// 不能自动创建目录
                savedir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());

            cropPhotoFile = new File(savedir.getAbsolutePath(), SAVE_PHONE_NAME_CROP
                + timeStamp+".jpg");
        }

    }

    /**
     * 根据根据扩展名获取类型
     *
     * @param attFormat
     * @return
     */
    public static String getContentType(String attFormat) {
        String contentType = FORMAT_TO_CONTENTTYPE.get("null");

        if (attFormat != null) {
            contentType = (String) FORMAT_TO_CONTENTTYPE.get(attFormat
                .toLowerCase());
        }
        return contentType;
    }



}