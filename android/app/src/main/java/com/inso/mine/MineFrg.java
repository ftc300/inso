package com.inso.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inso.R;
import com.inso.core.HttpMgr;
import com.inso.entity.http.SignUpResponse;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class MineFrg extends BaseFragment {


    @BindView(R.id.userIcon)
    ImageView mUserIcon;
    @BindView(R.id.userName)
    TextView mUserName;

    public static MineFrg getInstance() {
        return new MineFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_mine;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("我的");
        HttpMgr.getRequestQueue(mActivity).add(HttpMgr.getRequest(mActivity, BASE_URL + "member/info", new HttpMgr.IResponse<JSONObject>() {
            @Override
            public void onSuccess(JSONObject obj) {
                L.d("#######  getRequest onSuccess from " + BASE_URL + "member/info" + "\n" + obj.toString());
                SignUpResponse r = new Gson().fromJson(obj.toString(), SignUpResponse.class);
//                if (null != r) {
//                    if (!TextUtils.isEmpty(r.getProfile().getAvatar())) {
//                        Picasso.get()
//                                .load(r.getProfile().getAvatar())
//                                .placeholder(R.drawable.head_default)
//                                .error(R.drawable.head_default)
//                                .transform(new CropCircleTransformation())
//                                .into(mUserIcon);
//                    }
//                    mUserName.setText(TextUtils.isEmpty(r.getUsername()) ? r.getUser_id() : r.getUsername());
//                }
            }

            @Override
            public void onFail() {
//                ToastWidget.showFail(mActivity, "Fetch Error!");
            }
        }));
    }


    @OnClick({R.id.userIconLayout, R.id.mine_setting, R.id.mine_feedback, R.id.mine_about, R.id.mine_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userIconLayout:
                CommonAct.start(mActivity, MineInfoFrg.class);
                break;
            case R.id.mine_setting:
                CommonAct.start(mActivity, MineSettingFrg.class);
                break;
            case R.id.mine_feedback:
                CommonAct.start(mActivity, MineFeedbackFrg.class);
                break;
            case R.id.mine_about:
                CommonAct.start(mActivity, MineAboutFrg.class);
                break;
            case R.id.mine_logout:
                finish();
                break;
        }
    }

}
