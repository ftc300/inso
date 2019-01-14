package com.inshow.watch.android.act.debug.DebugStepStatistics;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.DebugStepDao;
import com.inshow.watch.android.dao.DebugStepPeriodDao;
import com.inshow.watch.android.fragment.IStepChangeListener;
import com.inshow.watch.android.provider.DebugDBHelper;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.InShowProgressDialog;
import com.inshow.watch.android.view.LabelTextRow;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static com.inshow.watch.android.manager.BleManager.B2I_getStep;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_TODAY_STEP;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.REQUEST_FAILED;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.TOKEN_NOT_MATCHED;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/20
 * @ 描述:
 */
public class DebugStepNumberAct extends BasicAct implements IStepChangeListener {

    private int mCurrentSteps;
    private TextView tvCurrentStep, tvComplete;
    private LabelTextRow lbType, lbGoal, lbHistory,lbHistory1;
    private long startTime, endTime;
    private int startStep, endStep;
    private int goalStep;
    private String[] source;
    private Button left, right;
    private MLAlertDialog.Builder singleChoiceBuilder;
    private MLAlertDialog.Builder inputBuilder;
    private int selectType;
    private static final int PERIOD_NAN = -1;
    private static final int PERIOD_START = -2;
    private static final int PERIOD_END = -3;
    private DebugDBHelper debugDBHelper;
    private DebugStepDao dao;
    private DebugStepPeriodDao periodDao;
    private InShowProgressDialog dialogInstance;
    private StepArcView cc;
    private TextView currentState;
    public static String ARG_CATCH_TYPE = "ARG_CATCH_TYPE";
    @Period
    private int mPeriod = PERIOD_NAN;

    @IntDef({PERIOD_NAN, PERIOD_START, PERIOD_END})
    @interface Period {
    }

    private int getDeltaStep() {
        return endStep - startStep;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_debug_step_numbers;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        dialogInstance = new InShowProgressDialog(mContext);
        dialogInstance.setCanceledOnTouchOutside(false);
        dialogInstance.setmIsCancelable(false);
        dialogInstance.setIndeterminate(true);
        dialogInstance.setMessage("设备连接中，请稍候...");
        dialogInstance.setCancelIntercepter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInstance.dismiss();
            }
        });
        dialogInstance.show();
        setBtnOnBackPress();
        setTitleText("实验功能-计步数统计");
        mReceiver.setStepChangeListener(this);
        initData();
        initView();
        XmBluetoothManager.getInstance().secureConnect(MAC, new Response.BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                dialogInstance.dismiss();
                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                    ToastUtil.showToastNoRepeat(mContext, "连接成功");
                    readCurrentStep();
                } else if (code == TOKEN_NOT_MATCHED) {
                    ToastUtil.showToastNoRepeat(mContext, "Remove Token，请退出插件重连");
                    XmBluetoothManager.getInstance().removeToken(MAC);
                } else if (code == REQUEST_FAILED) {
                    ToastUtil.showToastNoRepeat(mContext, "连接失败,请退出插件重连");
                }
            }
        });
    }

    private void initView() {
        tvCurrentStep = (TextView) findViewById(R.id.current);
        tvComplete = (TextView) findViewById(R.id.complete);
        currentState = (TextView) findViewById(R.id.current_state);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        right.setEnabled(false);
        lbType = (LabelTextRow) findViewById(R.id.step00);
        lbGoal = (LabelTextRow) findViewById(R.id.step01);
        lbHistory = (LabelTextRow) findViewById(R.id.step02);
        lbHistory1 = (LabelTextRow) findViewById(R.id.step03);
        lbType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleChoiceBuilder.setSingleChoiceItems(source, selectType, new MLAlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectType = which;
                        lbType.setText(source[which]);
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        lbGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputBuilder.setTitle("设置目标")
                        .setInputView("500", true)
                        .setInputView("请输入目标步数", true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input = inputBuilder.getInputView().getText().toString();
                                try {
                                    int ret = Integer.parseInt(input);
                                    if (ret >= 0) {
                                        lbGoal.setText(String.valueOf(ret));
                                        goalStep = ret;
                                        cc.setCurrentCount(goalStep,mCurrentSteps- startStep);
                                    } else {
                                        ToastUtil.showToastNoRepeat(mContext, "请输入自然数");
                                    }
                                } catch (Exception e) {
                                    ToastUtil.showToastNoRepeat(mContext, "请输入自然数");
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        lbHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> extras = new HashMap<>();
                extras.put(ARG_CATCH_TYPE,0);
                switchTo(DebugHistoryStepAct.class,extras);
            }
        });

        lbHistory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> extras = new HashMap<>();
                extras.put(ARG_CATCH_TYPE,1);
                switchTo(DebugHistoryStepAct.class,extras);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setEnabled(false);
                right.setEnabled(true);
                currentState.setText("测试中");
                mPeriod = PERIOD_START;
                startTime = System.currentTimeMillis();
                startStep = mCurrentSteps;
                periodDao.type = selectType;
                periodDao.period = PERIOD_START;
                periodDao.startstep = mCurrentSteps;
                periodDao.starttime = startTime;
                periodDao.mac = MAC;
                debugDBHelper.replaceDebugStepPeriod(periodDao);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setEnabled(true);
                right.setEnabled(false);
                endTime = System.currentTimeMillis();
                mPeriod = PERIOD_END;
                endStep = mCurrentSteps;
                currentState.setText("测试结束");
                ToastUtil.showToastNoRepeat(mContext, "测试完成");
                periodDao.period = PERIOD_END;
                debugDBHelper.replaceDebugStepPeriod(periodDao);
                dao = new DebugStepDao(goalStep, startTime, endTime, startStep, endStep, selectType, MAC);
                debugDBHelper.addDebugStep(dao);
            }
        });
    }

    private void initData() {
        cc = (StepArcView) findViewById(R.id.cc);
        debugDBHelper = new DebugDBHelper(this);
        source = getResources().getStringArray(R.array.debug_step);
        singleChoiceBuilder = new MLAlertDialog.Builder(this);
        inputBuilder = new MLAlertDialog.Builder(this);
        goalStep = 500;
        periodDao = debugDBHelper.getDebugStepPeriodDao(MAC);
        cc.setCurrentCount(500,0);
        if (periodDao == null) {
            periodDao = new DebugStepPeriodDao();
        } else {
            if (periodDao.period == PERIOD_START) { //开始过
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage("您有一项测试正在进行中，继续完成测试？")
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectType = periodDao.type;
                                startStep = periodDao.startstep;
                                startTime = periodDao.starttime;
                                endStep = mCurrentSteps;
                                mPeriod = PERIOD_START;
                                lbType.setText(source[selectType]);
                                left.setEnabled(false);
                                right.setEnabled(true);
                                currentState.setText("测试中");
                            }
                        }).setNegativeButton("重新开始测试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        debugDBHelper.deleteDebugStepPeriod(MAC);
                        left.setEnabled(true);
                        right.setEnabled(false);
                    }
                }).show();
            }
        }
    }

    private void readCurrentStep() {
        XmBluetoothManager.getInstance().notify(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_TODAY_STEP), new Response.BleNotifyResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_TODAY_STEP), new Response.BleReadResponse() {
            @Override
            public void onResponse(int i, byte[] bytes) {
                onChanged(bytes);
                endStep = mCurrentSteps;
            }
        });
    }

    @Override
    public void onChanged(byte[] value) {
        int[] i = B2I_getStep(value);
        mCurrentSteps = i[0];
        tvCurrentStep.setText(String.valueOf(mCurrentSteps));
        if(mPeriod == PERIOD_START) {
            tvComplete.setText(String.valueOf(mCurrentSteps - startStep));
            cc.setCurrentCount(goalStep,mCurrentSteps - startStep);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        debugDBHelper.close();
    }

    @Override
    public void onDestroy() {
        if (XmBluetoothManager.getInstance().getConnectStatus(MAC) == XmBluetoothManager.STATE_CONNECTED) {
            XmBluetoothManager.getInstance().disconnect(MAC, 0);
        }
        super.onDestroy();
    }
}
