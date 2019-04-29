package com.inso.plugin.act.more.adjust;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.plugin.basic.BasicAct;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inso.plugin.manager.BleManager.setAdjustH;
import static com.inso.plugin.manager.BleManager.setAdjustM;
import static com.inso.plugin.manager.BleManager.setAdjustPointerPosition;
import static com.inso.plugin.manager.BleManager.setAdjustSwitch;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CONTROL;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/29
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class AdjustDebug extends BasicAct {
    @BindView(R.id.hourPointer)
    Button mHourPointer;
    @BindView(R.id.minPointer)
    Button mMinPointer;
    @BindView(R.id.neHourPointer)
    Button mNeHourPointer;
    @BindView(R.id.neMinPointer)
    Button mNeMinPointer;
    private boolean isLongOnClickH, isLongOnClickM,isLongOnClickNH, isLongOnClickNM;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_adjust_debug;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText(getString(R.string.adjust_ponit));
        setActStyle(ActStyle.WT);
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustSwitch(false));
        mHourPointer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongOnClickH = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isLongOnClickH) {
                            try {
                                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustH(15));
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                return true;
            }
        });
        mMinPointer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongOnClickM = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isLongOnClickM) {
                            try {
                                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustM(15));
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                return true;
            }
        });
        mNeHourPointer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongOnClickNH = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isLongOnClickNH) {
                            try {
                                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustH(-15));
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                return true;
            }
        });
        mNeMinPointer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongOnClickNM = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isLongOnClickNM) {
                            try {
                                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustM(-15));
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                return true;
            }
        });
        mHourPointer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isLongOnClickH = false;
                }
                return false;
            }
        });
        mMinPointer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isLongOnClickM = false;
                }
                return false;
            }
        });
        mNeHourPointer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isLongOnClickNH = false;
                }
                return false;
            }
        });
        mNeMinPointer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isLongOnClickNM = false;
                }
                return false;
            }
        });

    }


    @OnClick({R.id.hourPointer, R.id.minPointer, R.id.ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hourPointer:
                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustH(5));
                break;
            case R.id.minPointer:
                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustM(5));
                break;
            case R.id.neHourPointer:
                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustH(-5));
                break;
            case R.id.neMinPointer:
                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustM(-5));
                break;
            case R.id.ok:
                BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustPointerPosition(0, 0));
                finish();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BleMgr.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), setAdjustSwitch(true));
    }

}
