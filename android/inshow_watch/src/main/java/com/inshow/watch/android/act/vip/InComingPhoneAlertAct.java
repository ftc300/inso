package com.inshow.watch.android.act.vip;

import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.LowPowerManager;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.ChangeUI;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpVipState;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.Rom;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.Callback;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import static com.inshow.watch.android.event.ChangeUI.RENDER_AGAIN;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_INCOMING_SWITCH;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/11/22
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class InComingPhoneAlertAct extends BasicAct {

    private TextView topTv, centerTv;
    private ImageView img;
    private boolean originState;
    private boolean currentState;
    private CheckBox switchButton;


    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_transparent_white_remind;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_incoming_phone;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentState) {
            LowPowerManager.getInstance().tipOnlyOnce(mContext);
        }
    }

    @Override
    protected void initViewOrData() {
        if (!Rom.isMIUI()) {
            new MLAlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setMessage(getString(R.string.sorry_not_support_vip))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
        setBtnOnBackPress();
        bindView();
    }

    void bindView(){
        ((TextView) mTitleView.findViewById(R.id.title_bar_title)).setText(getString(R.string.vip_alert));
        centerTv = (TextView) findViewById(R.id.centerTip);
        topTv = (TextView) findViewById(R.id.topTip);
        img = (ImageView) findViewById(R.id.img);
        switchButton = (CheckBox) findViewById(R.id.switchButton);
        currentState = (Boolean) SPManager.get(mContext,SP_INCOMING_SWITCH,false);
        originState = currentState;
        switchButton.setChecked(currentState);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needPush = true;
                currentState = !currentState;
                SPManager.put(mContext,SP_INCOMING_SWITCH,currentState);
                SyncDeviceHelper.changeInComingAlertState(MAC,currentState);
                switchButton.setChecked(currentState);
                renderByState();
                EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
            }
        });
        renderByState();
    }

    void renderByState(){
        centerTv.setVisibility(currentState? View.GONE:View.VISIBLE);
        topTv.setVisibility(currentState? View.VISIBLE:View.GONE);
        img.setVisibility(currentState? View.VISIBLE:View.GONE);
    }


    @Override
    public void onPause() {
        if (needPush) {
            mDBHelper.updateTimeStamp(VIP_KEY, TimeUtil.getNowTimeSeconds());
            pushVipInfoToMijia();
        }
        super.onPause();
    }

    /**
     * 上传Vip信息
     */
    private void pushVipInfoToMijia() {
        boolean b = (Boolean) SPManager.get(mContext, SP_INCOMING_SWITCH, false);
        HttpVipState vipState = new HttpVipState(b ? "on" : "off");
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        VIP_KEY,
                        AppController.getGson().toJson(vipState),
                        mSyncHelper.getLocalVipTime()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushVipInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushVipInfoToMijia onFailure:" + s);
                    }
                });
    }
}
