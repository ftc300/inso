package com.inso.core;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.transformation.CropCircleTransformation;
import com.inso.entity.http.UserInfo;
import com.inso.plugin.manager.SPManager;
import com.inso.watch.baselib.wigets.LabelTextRow;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static com.inso.core.Constant.CACHE_KEY_USER_INFO;
import static com.inso.core.Constant.SP_ACCESS_TOKEN;
import static com.inso.core.Constant.SP_EXPIRED_AT;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/15
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class UserMgr {

    private UserMgr() {
    }

    public static UserMgr getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static boolean isExpired(Context context) {
        long expired_at = (Long) SPManager.get(context, SP_EXPIRED_AT, 0L);
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        return now <= expired_at;
    }

    private static class SingletonHolder {
        private static final UserMgr INSTANCE = new UserMgr();
    }


    public static String getAccessToken(Context context) {
        return (String) SPManager.get(context, SP_ACCESS_TOKEN, "");
    }

    public static void saveUserInfo(CacheMgr mCache, UserInfo info) {
        mCache.put(CACHE_KEY_USER_INFO, info);
    }

    public static UserInfo getUserInfo(CacheMgr mCache) {
        return (UserInfo) mCache.getAsObject(CACHE_KEY_USER_INFO);
    }

    public static void showUserIcon(UserInfo userInfo, ImageView imgView) {
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            Picasso.get()
                    .load(userInfo.getAvatar())
                    .placeholder(R.drawable.pic_avatar_default)
                    .transform(new CropCircleTransformation())
                    .into(imgView);
        }
    }


    public static void showNickName(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getNickname())) {
            textRow.setText(userInfo.getNickname());
        }
    }
    public static void showUserId(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getUser_id())) {
            textRow.setText(userInfo.getUser_id());
        }
    }
    public static void showGender(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getGender())) {
            textRow.setText(userInfo.getGender());
        }
    }
    public static void showBirth(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getBirth())) {
            textRow.setText(userInfo.getBirth());
        }
    }
    public static void showHeight(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getHeight())) {
            textRow.setText(userInfo.getHeight());
        }
    }

    public static void showWeight(UserInfo userInfo, LabelTextRow textRow){
        if (!TextUtils.isEmpty(userInfo.getWeight())) {
            textRow.setText(userInfo.getWeight());
        }
    }

}
