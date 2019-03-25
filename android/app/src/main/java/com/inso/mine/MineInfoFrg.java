package com.inso.mine;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.CacheMgr;
import com.inso.core.Constant;
import com.inso.core.HttpMgr;
import com.inso.core.MediaManager;
import com.inso.core.UIManager;
import com.inso.core.UserMgr;
import com.inso.entity.http.UserInfo;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.wigets.LabelTextRow;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inso.core.UserMgr.showUserIcon;
import static com.inso.watch.baselib.Constants.BASE_URL;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class MineInfoFrg extends BaseFragment implements IUserIconResponse {

    @BindView(R.id.userId)
    LabelTextRow mUserId;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.userIcon)
    ImageView mUserIcon;
    @BindView(R.id.userIconLayout)
    LinearLayout mUserIconLayout;
    @BindView(R.id.nickName)
    LabelTextRow mNickName;
    @BindView(R.id.gender)
    LabelTextRow mGender;
    @BindView(R.id.birth)
    LabelTextRow mBirth;
    @BindView(R.id.height)
    LabelTextRow mHeight;
    @BindView(R.id.weight)
    LabelTextRow mWeight;
    @BindView(R.id.mobile)
    LabelTextRow mMobile;
    @BindView(R.id.changePwd)
    LabelTextRow mChangePwd;
    private static String image;
    private Dialog uploadPhoto;
    private MineInfoHandler handImage;
    private static CacheMgr mCache;
    private static UserInfo mUserInfo;
    private Handler mainHandler;

    @Override
    protected int getContentRes() {
        return R.layout.frg_mine_info;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle(true, "我的信息");
        mainHandler = new Handler(Looper.getMainLooper());
        handImage = new MineInfoHandler(mActivity, this, this);
        mCache = CacheMgr.get(mActivity);
        mUserInfo = UserMgr.getUserInfo(mCache);
        if (null != mUserInfo) {
            showUserIcon(mUserInfo.getAvatar(),mUserIcon);
        }
    }


    @OnClick({R.id.userId, R.id.userName, R.id.userIconLayout, R.id.nickName, R.id.gender, R.id.birth, R.id.height, R.id.weight, R.id.mobile, R.id.changePwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userId:
                break;
            case R.id.userName:
                break;
            case R.id.userIconLayout:
                View v = View.inflate(mActivity, R.layout.act_get_photo, null);
                uploadPhoto = UIManager.getActionSheet(mActivity, v);
                uploadPhoto.findViewById(R.id.action_take).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaManager.getPhotoFromAlbum(MineInfoFrg.this);
                        uploadPhoto.dismiss();
                    }
                });
                uploadPhoto.findViewById(R.id.action_pick).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndPermission.with(mActivity)
                                .runtime()
                                .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .onGranted(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {
                                        MediaManager.getPhotoFromCamera(MineInfoFrg.this);
                                        uploadPhoto.dismiss();
                                    }
                                })
                                .onDenied(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {
                                    }
                                })
                                .start();

                    }
                });
                uploadPhoto.show();
                break;
            case R.id.nickName:
                break;
            case R.id.gender:
                break;
            case R.id.birth:
                break;
            case R.id.height:
                break;
            case R.id.weight:
                break;
            case R.id.mobile:
                break;
            case R.id.changePwd:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MediaManager.onActivityResult(this, handImage, requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(final String url) {
        L.d("onSuccess");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                showUserIcon(url,mUserIcon);
            }
        });
    }

    @Override
    public void onFail() {
        L.d("onFail");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastWidget.showFail(mActivity, "头像上传失败");
            }
        });
    }

    static class MineInfoHandler extends Handler {
        //WeakReference to the outer class's instance.
        private WeakReference<MineInfoFrg> mOuter;
        private IUserIconResponse mResponse;
        private Context mContext;

        public MineInfoHandler(Context context, MineInfoFrg frg, IUserIconResponse response) {
            mContext = context;
            mOuter = new WeakReference<>(frg);
            mResponse = response;
        }

        @Override
        public void handleMessage(Message msg) {
            MineInfoFrg outer = mOuter.get();
            if (outer != null) {
                if (msg.what == Constant.MSG_SHOW_PHOTO) {
                    image = (String) msg.obj;
                    L.d("select or take photo image :" + image);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpMgr.postUploadImageRequest(BASE_URL + "member/avatar?access_token=" + UserMgr.getAccessToken(mContext), new File(image), new HttpMgr.IResponse<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    L.d("upload image success ,response:" + s);
                                    try {
                                        String avatar = new JSONObject(s).getString("url");
                                        L.d("upload image success ,response avatar:" + avatar);
                                        mUserInfo.setAvatar(avatar);
                                        UserMgr.saveUserInfo(mCache, mUserInfo);
                                        mResponse.onSuccess(avatar);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail() {
                                    L.e("upload image fail");
                                    mResponse.onFail();
                                }
                            });
                        }
                    }).start();
                }
            }
        }
    }


}
