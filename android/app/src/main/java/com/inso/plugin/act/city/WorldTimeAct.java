package com.inso.plugin.act.city;

import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.XmBluetoothManager;
import com.inso.plugin.adapter.WorldTimeListAdp;
import com.inso.plugin.basic.BasicListAct;
import com.inso.plugin.dao.PreferCitiesDao;
import com.inso.plugin.event.ChangeUI;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.Configuration;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.TextStyle;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.tools.ToastUtil;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.inso.plugin.event.ChangeUI.RENDER_AGAIN;
import static com.inso.plugin.manager.BleManager.I2B_SyncTime;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.*;
import static com.inso.plugin.tools.Constants.SystemConstant.*;
import static com.inso.plugin.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by chendong on 2017/1/22.
 * 世界时间
 */
public class WorldTimeAct extends BasicListAct {
    private ListView lv;
    private WorldTimeListAdp adp;
    private Button btnAdd;
    private View customTitle;
    private TextView customTextView;
    private final int PREFER_POS = 0;
    private final int DELETE_POS = 1;
    private final int CANCEL_POS = 2;
    private int preferPos = CANCEL_POS;
    private TextStyle mTs;
    private List<PreferCitiesDao> dataSource = new ArrayList<>();
    private ImageView imageView;
    private Animation operatingAnim;
    private LinearInterpolator lin;
    private LinearLayout ll;
    private View footerView;
    private TextView footerTextView;
    private Timer timer;
    private int DEFAULT_ID;

    @Override
    protected String getEmptyString() {
        return getString(R.string.empty_tip_worldtime);
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_worldtime;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText(getString(R.string.title_worldtime));
        mTs = new TextStyle(getResources().getColor(R.color.black_60_transparent), 20);
        lv = (ListView) findViewById(R.id.listView);
        btnAdd = (Button) findViewById(R.id.add);
        adp = new WorldTimeListAdp(this);
//        footerView = getLayoutInflater().inflate(R.layout.watch_list_footer, null);
//        footerTextView = (TextView) footerView.findViewById(R.id.tv_empty);
//        footerTextView.setVisibility(View.GONE);
//        lv.addFooterView(footerView);
        Configuration.ServerHandle(new Configuration.ServerHandler() {
            @Override
            public String defaultServer() {
                DEFAULT_ID = BJID;
                return null;
            }

            @Override
            public String cnServer() {
                DEFAULT_ID = BJID;
                return null;
            }

            @Override
            public String twServer() {
                DEFAULT_ID = TBID;
                return null;
            }

            @Override
            public String hkServer() {
                DEFAULT_ID = HKID;
                return null;
            }
        });
        lv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showItemOperation(view,position);
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                showItemOperation(view, i);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDBHelper.getAllPreferCities().size() < 10) {
                    needPush = true;
                    switchTo(CitySelectAct.class);
                } else {
                    ToastUtil.showToastNoRepeat(mContext, getString(R.string.city_most_tip));
                }

            }
        });
    }

    private void showItemOperation(final View view, final int i) {
        if (i == adp.getCount()) return;
        customTitle = View.inflate(WorldTimeAct.this, R.layout.wacth_dialog_custom_title, null);
        customTextView = (TextView) customTitle.findViewById(R.id.title);
        final PreferCitiesDao item = dataSource.get(i);
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                customTextView.setText(mTs.clear().spanColorAndSize(TimeUtil.getHHMM(item.zone) + "\n").spanColor(item.zh_cn + "\t\t" + TimeUtil.getMonDay(mContext, item.zone)).getText());
            }

            @Override
            public void twHandle() {
                customTextView.setText(mTs.clear().spanColorAndSize(TimeUtil.getHHMM(item.zone) + "\n").spanColor(item.zh_tw + "\t\t" + TimeUtil.getMonDay(mContext, item.zone)).getText());
            }

            @Override
            public void hkHandle() {
                customTextView.setText(mTs.clear().spanColorAndSize(TimeUtil.getHHMM(item.zone) + "\n").spanColor(item.zh_hk + "\t\t" + TimeUtil.getMonDay(mContext, item.zone)).getText());
            }

            @Override
            public void enHandle() {
                customTextView.setText(mTs.clear().spanColorAndSize(TimeUtil.getHHMM(item.zone) + "\n").spanColor(item.en + "\t\t" + TimeUtil.getMonDay(mContext, item.zone)).getText());
            }

            @Override
            public void defaultHandle() {
                customTextView.setText(mTs.clear().spanColorAndSize(TimeUtil.getHHMM(item.zone) + "\n").spanColor(item.zh_cn + "\t\t" + TimeUtil.getMonDay(mContext, item.zone)).getText());
            }
        });
        new MLAlertDialog.Builder(WorldTimeAct.this)
                .setCustomTitle(customTitle)
                .setItems(getResources().getStringArray(adp.getItem(i).isDefault ? R.array.worldtime_list1 : R.array.worldtime_list0), new MLAlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needPush = true;
                        preferPos = which;
                        if (which == PREFER_POS) {//第一项为设置手表时间
//                                    if (!adp.getItem(i).isSel) {
//                                        ToastUtil.showToastNoRepeat(mContext, getString(R.string.adjust_time_ing));
//                                    }
                            ll = (LinearLayout) adp.getView(i, view, null);
                            imageView = (ImageView) ll.findViewById(R.id.loading);
                            operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.centerrotation);
                            lin = new LinearInterpolator();
                            operatingAnim.setInterpolator(lin);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.startAnimation(operatingAnim);
                        }
                    }
                }).setDismissCallBack(new MLAlertDialog.DismissCallBack() {
                                          @Override
                                          public void beforeDismissCallBack() {
                                          }

                                          @Override
                                          public void afterDismissCallBack() {
                                              if (preferPos == PREFER_POS) {
                                                  ToastUtil.showToast(mContext, getString(R.string.time_has_adjusted));
                                                  mDBHelper.updateSelPreferCity((int) adp.getItem(i).id);
                                                  renderListView();
                                                  L.e( " TimeUtil.getNowTimeSeconds(item.zone):" + TimeUtil.getNowTimeSeconds(item.zone) );
                                                  L.e( "TimeUtil.getWatchSysStartTimeSecs() :" +  TimeUtil.getWatchSysStartTimeSecs());
                                                  L.e("TimeUtil.getNowTimeSeconds(item.zone) - TimeUtil.getWatchSysStartTimeSecs() : " + (TimeUtil.getNowTimeSeconds(item.zone) - TimeUtil.getWatchSysStartTimeSecs()));
                                                  XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME), I2B_SyncTime(TimeUtil.getNowTimeSeconds(item.zone) - TimeUtil.getWatchSysStartTimeSecs()), new XmBluetoothManager.IWriteResponse() {
                                                      @Override
                                                      public void onSuccess() {
                                                          imageView.clearAnimation();

                                                      }

                                                      @Override
                                                      public void onFail() {

                                                      }
                                                  });
                                              } else if (!adp.getItem(i).isDefault && preferPos == DELETE_POS) {
                                                  L.e("adp.getItem(i).isSel " + adp.getItem(i).isSel);
                                                  if (adp.getItem(i).isSel){//删除手表时间时 恢复到北京时间
                                                      Configuration.ServerHandle(new Configuration.ServerHandler() {
                                                          @Override
                                                          public String defaultServer() {
                                                              ToastUtil.showToast(mContext, getString(R.string.adjust_time_to_beijing));
                                                              return null;
                                                          }

                                                          @Override
                                                          public String cnServer() {
                                                              ToastUtil.showToast(mContext, getString(R.string.adjust_time_to_beijing));
                                                              return null;
                                                          }

                                                          @Override
                                                          public String twServer() {
                                                              ToastUtil.showToast(mContext, getString(R.string.adjust_time_to_tw));
                                                              return null;
                                                          }

                                                          @Override
                                                          public String hkServer() {
                                                              ToastUtil.showToast(mContext, getString(R.string.adjust_time_to_hk));
                                                              return null;
                                                          }
                                                      });
//                                                      写入北京时间 成功操作数据库
                                                      XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME), I2B_SyncTime(TimeUtil.getNowTimeSeconds() - TimeUtil.getWatchSysStartTimeSecs()), new XmBluetoothManager.IWriteResponse() {
                                                          @Override
                                                          public void onSuccess() {
                                                              ToastUtil.showToastNoRepeat(mContext, getString(R.string.time_has_adjusted));
                                                          }

                                                          @Override
                                                          public void onFail() {
                                                              ToastUtil.showToastNoRepeat(mContext, getString(R.string.adjust_time_fail));
                                                          }
                                                      });
                                                  } else {
                                                      deleteAndRender(false, i);
                                                  }
                                              }
                                              preferPos = CANCEL_POS;
                                          }
                                      }
        ).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        renderListView();
    }


    /**
     * 删除某项
     *
     * @param isSel
     * @param i
     */
    private void deleteAndRender(boolean isSel, int i) {
        needPush = true;
        if (isSel) {
//            mDBHelper.updateSelPreferCity(Configuration.getServerID());//北京时间id为1
            mDBHelper.updateSelPreferCity(DEFAULT_ID);
        }
        mDBHelper.deletePreferCityByID((int) adp.getItem(i).id);
        renderListView();
    }

    /**
     * 每次从数据库重新刷新数据源并更新listview
     */
    private void renderListView() {
        dataSource = mDBHelper.getAllPreferCities();
        adp.setData(dataSource);
        lv.setAdapter(adp);
        for (int i = 0; i < dataSource.size(); i++) {
            if (adp.getItem(i).isSel) {
                adp.setPosSelected(i);
                break;
            }
        }
//         btnAdd.setEnabled(dataSource.size()<10);
//        if(dataSource.size()>0) {
//            if(lv.getFooterViewsCount()==0) lv.addFooterView(footerView);
//            footerTextView.setText("还可以添加"+(10-dataSource.size())+"个城市");
//        }
//        if(dataSource.size()==0 && lv.getFooterViewsCount()>0){
//             lv.removeFooterView(footerView);
//        }
//        adp.notifyDataSetChanged();
        if (null == timer) {
            timer = new Timer();
            invalidateList();
        }
    }

    private void invalidateList() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((System.currentTimeMillis() / 1000) % 60 == 0) {//60秒更新一次
                            adp.notifyDataSetChanged();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
        if ((needPush && mBackFlag) || !(Boolean) SPManager.get(mContext, SP_ARG_HAS_DEFAULT_CITY, true)) {
            mDBHelper.updateTimeStamp(WORLD_CITY_KEY, TimeUtil.getNowTimeSeconds());
            pushWorldCityToMijia();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
    }

    private void pushWorldCityToMijia() {
//        List<HttpWorldCity> list = new ArrayList<>();
//        List<PreferCitiesDao> dbSrc = mDBHelper.getAllPreferCities();
//        for (PreferCitiesDao item : dbSrc) {
//            list.add(new HttpWorldCity(item.id, item.isSel, item.zh_cn, item.zh_tw, item.zh_hk, item.zone, item.en));
//        }
//        HttpSyncHelper.pushData(
//                new RequestParams(
//                        MODEL,
//                        UID,
//                        DID,
//                        TYPE_USER_INFO,
//                        WORLD_CITY_KEY,
//                        AppController.getGson().toJson(list),
//                        mSyncHelper.getLocalWorldCityKeyTime()), new Callback<JSONArray>() {
//                    @Override
//                    public void onSuccess(JSONArray jsonArray) {
//                        L.e("pushWorldCityToMijia:" + jsonArray.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//                        L.e("pushWorldCityToMijiaError:" + s);
//                    }
//                });
    }

}
