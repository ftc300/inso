package com.inso.plugin.act.more;

import android.view.View;

import com.inso.R;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.MessUtil;
import com.inso.plugin.tools.ToastUtil;
import com.inso.plugin.view.LabelTextRow;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_FIRMWARE_VERSION;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_DEBUG_FLAG;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_DEBUG_WATCHLOG_FLAG;


/**
 * Created by chendong on 2017/1/22.
 */
public class DeviceInfoAct extends BasicAct {

    LabelTextRow model, firmware, bluetooth, battery, watchlog;
    private int count ,logCount;
    private boolean isDebug = true;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_deviceinfo;
    }

    @Override
    protected void initViewOrData() {
        setTitleText(getString(R.string.device_info));
        setBtnOnBackPress();
        setActStyle(ActStyle.WT);
        model = (LabelTextRow) findViewById(R.id.model);
        firmware = (LabelTextRow) findViewById(R.id.firmware);
        bluetooth = (LabelTextRow) findViewById(R.id.bluetooth);
        battery = (LabelTextRow) findViewById(R.id.battery);
        watchlog = (LabelTextRow) findViewById(R.id.watchlog);
        model.setText("SYB01");
        L.e("SP_ARG_FIRMWARE_VERSION:"+ SPManager.get(mContext, SP_ARG_FIRMWARE_VERSION, "") );
        L.e("SP_ARG_FIRMWARE_VERSION DB:"+mDBHelper.getCache(SP_ARG_FIRMWARE_VERSION ));
        firmware.setText(mDBHelper.getCache(SP_ARG_FIRMWARE_VERSION ));
        bluetooth.setText(MAC);
        battery.setText("CR2430");
        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MessUtil.isWhiteList(UID)) {
                    if ((Boolean) SPManager.get(mContext, SP_DEBUG_FLAG, false)) {
                        if (isDebug) {
                            ToastUtil.showToastNoRepeat(mContext, "已经处于调试模式，请返回查看菜单！");
                            isDebug = false;
                        }
                    } else {
                        count++;
                        if (count > 6) {
                            SPManager.put(mContext, SP_DEBUG_FLAG, true);
                        }
                    }
                }
            }
        });
        battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) SPManager.get(mContext, SP_DEBUG_WATCHLOG_FLAG, false)) {
                    watchlog.setVisibility(View.VISIBLE);
                    watchlog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            switchTo(WatchLogAct.class);
                        }
                    });
                } else {
                    logCount++;
                    if (logCount > 6) {
                        SPManager.put(mContext, SP_DEBUG_WATCHLOG_FLAG, true);
                    }
                }
            }
        });
    }
}
