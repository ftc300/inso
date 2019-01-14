package com.inshow.watch.android.act.setting;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.WatchUserDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpUserInfo;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.NumUtil;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.LabelTextRow;
import com.inshow.watch.android.view.WatchNumberPicker;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.SettingHelper.BIRTH_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.GENDER_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.HEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.WEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BIRTH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_GENDER;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_WEIGHT;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.view.WatchNumberPicker.TWO_DIGIT_FORMATTER;

/**
 * Created by chendong on 2017/1/22.
 */
public class BodyInfoAct extends BasicAct {

    private LabelTextRow birth, sex, height, weight;
    private WatchUserDao user;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_bodyinfo;
    }

    @Override
    protected void initViewOrData() {
        setTitleText(getString(R.string.body_info));
        setBtnOnBackPress();
        setActStyle(ActStyle.WT);
        birth = (LabelTextRow) findViewById(R.id.birth);
        sex = (LabelTextRow) findViewById(R.id.sex);
        height = (LabelTextRow) findViewById(R.id.height);
        weight = (LabelTextRow) findViewById(R.id.weight);
        renderUI();
        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthStr = (String) SPManager.get(mContext, SP_ARG_BIRTH, BIRTH_DEFAULT);
                View v = View.inflate(mContext, R.layout.watch_dialog_twoline, null);
                final WatchNumberPicker year = (WatchNumberPicker) v.findViewById(R.id.lp1);
                final WatchNumberPicker month = (WatchNumberPicker) v.findViewById(R.id.lp2);
                year.setMaxValue(2017);
                year.setMinValue(1917);
                year.setFormatter(TWO_DIGIT_FORMATTER);
                if (birthStr != null && birthStr.contains("-"))
                    year.setValue(Integer.parseInt(birthStr.split("-")[0]));
                month.setMaxValue(12);
                month.setMinValue(1);
                month.setFormatter(TWO_DIGIT_FORMATTER);
                if (birthStr != null && birthStr.contains("-"))
                    month.setValue(Integer.parseInt(birthStr.split("-")[1]));
                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.set_birthday)).setView(v).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        needPush = true;
                        SPManager.put(mContext, SP_ARG_BIRTH, year.getValue() + "-" + NumUtil.formatTwoDigitalNum(month.getValue()));
                        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                            @Override
                            public void cnHandle() {
                                birth.setText(year.getValue() + getString(R.string.unit_year) + month.getValue() + getString(R.string.unit_month));
                            }

                            @Override
                            public void twHandle() {
                                birth.setText(year.getValue() + getString(R.string.unit_year) + month.getValue() + getString(R.string.unit_month));
                            }

                            @Override
                            public void hkHandle() {
                                birth.setText(year.getValue() + getString(R.string.unit_year) + month.getValue() + getString(R.string.unit_month));
                            }

                            @Override
                            public void enHandle() {
                                birth.setText(year.getValue() + "." + NumUtil.formatTwoDigitalNum(month.getValue()));
                            }

                            @Override
                            public void defaultHandle() {
                                birth.setText(year.getValue() + getString(R.string.unit_year) + month.getValue() + getString(R.string.unit_month));
                            }
                        });
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });
        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String genderStr = (String) SPManager.get(mContext, SP_ARG_GENDER, GENDER_DEFAULT);
                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.set_gender)).setSingleChoiceItems(new String[]{getString(R.string.nv), getString(R.string.nan)}, TextUtils.equals(genderStr, "male") ? 1 : 0, new MLAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needPush = true;
                        SPManager.put(mContext, SP_ARG_GENDER, which == 0 ? "female" : "male");
                        sex.setText(which == 0 ? getString(R.string.nv) : getString(R.string.nan));
                        dialog.dismiss();
                    }
                })
                        .show();
            }
        });
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(mContext, R.layout.watch_dialog_line, null);
                final WatchNumberPicker lp = (WatchNumberPicker) v.findViewById(R.id.lp);
                lp.setMaxValue(242);
                lp.setMinValue(30);
                lp.setValue((int) SPManager.get(mContext, SP_ARG_HEIGHT, HEIGHT_DEFAULT));
                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.setHeight)).setView(v).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        needPush = true;
                        SPManager.put(mContext, SP_ARG_HEIGHT, lp.getValue());
                        height.setText(lp.getValue() + getString(R.string.unit_limi));
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(mContext, R.layout.watch_dialog_line, null);
                final WatchNumberPicker lp = (WatchNumberPicker) v.findViewById(R.id.lp);
                lp.setMaxValue(250);
                lp.setMinValue(3);
                lp.setValue((int) SPManager.get(mContext, SP_ARG_WEIGHT, WEIGHT_DEFAULT));
                lp.setLabel(getString(R.string.unit_gongjin_space));
                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.set_weight)).setView(v).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        needPush = true;
                        SPManager.put(mContext, SP_ARG_WEIGHT, lp.getValue());
                        weight.setText(lp.getValue() + getString(R.string.unit_gongjin));
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

            }
        });
    }

    private void renderUI() {
        user = mDBHelper.getWatchUserInfo();
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                birth.setText(user.birth.split("-")[0] + getString(R.string.unit_year) + user.birth.split("-")[1] + getString(R.string.unit_month));
            }

            @Override
            public void twHandle() {
                birth.setText(user.birth.split("-")[0] + getString(R.string.unit_year) + user.birth.split("-")[1] + getString(R.string.unit_month));
            }

            @Override
            public void hkHandle() {
                birth.setText(user.birth.split("-")[0] + getString(R.string.unit_year) + user.birth.split("-")[1] + getString(R.string.unit_month));
            }

            @Override
            public void enHandle() {
                birth.setText(user.birth.split("-")[0] + "." + user.birth.split("-")[1]);
            }

            @Override
            public void defaultHandle() {
                birth.setText(user.birth.split("-")[0] + getString(R.string.unit_year) + user.birth.split("-")[1] + getString(R.string.unit_month));
            }
        });

        Configuration.getInstance().ServerHandle(new Configuration.ServerHandler2() {
            @Override
            public void defaultServer() {
                birth.setVisibility(View.GONE);
            }

            @Override
            public void cnServer() {
            }
        });
        sex.setText(user.gender.equals("male") ? getString(R.string.nan) :getString(R.string.nv));
        height.setText(user.height + getString(R.string.unit_limi));
        weight.setText(user.weight + getString(R.string.unit_gongjin));
        SPManager.put(mContext, SP_ARG_BIRTH, user.birth);
        SPManager.put(mContext, SP_ARG_HEIGHT, user.height);
        SPManager.put(mContext, SP_ARG_WEIGHT, user.weight);
        SPManager.put(mContext, SP_ARG_GENDER, user.gender);
    }


    @Override
    public void onPause() {
        if (needPush && mBackFlag) {
            mDBHelper.updateUser(new WatchUserDao(
                    (int) SPManager.get(mContext, SP_ARG_HEIGHT, HEIGHT_DEFAULT),
                    (int) SPManager.get(mContext, SP_ARG_WEIGHT, WEIGHT_DEFAULT),
                    (String) SPManager.get(mContext, SP_ARG_GENDER, GENDER_DEFAULT),
                    (String) SPManager.get(mContext, SP_ARG_BIRTH, BIRTH_DEFAULT)
            ));
            mDBHelper.updateTimeStamp(USER_KEY, TimeUtil.getNowTimeSeconds());
            SPManager.put(mContext, USER_KEY, TimeUtil.getNowTimeSeconds());
            pushBodyInfoToMijia();
        }
        super.onPause();
    }


    /**
     * 上传身体信息
     */
    private void pushBodyInfoToMijia() {
        HttpUserInfo bean = new HttpUserInfo(
                (int) SPManager.get(mContext, SP_ARG_WEIGHT, WEIGHT_DEFAULT),
                (int) SPManager.get(mContext, SP_ARG_HEIGHT, HEIGHT_DEFAULT),
                (String) SPManager.get(mContext, SP_ARG_GENDER, GENDER_DEFAULT),
                (String) SPManager.get(mContext, SP_ARG_BIRTH, BIRTH_DEFAULT)
        );
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        USER_KEY,
                        AppController.getGson().toJson(bean),
                        mDBHelper.getKeyTimeStamp(USER_KEY)), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushBodyInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushBodyInfoToMijiaError:" + s);
                    }
                });
    }
}
