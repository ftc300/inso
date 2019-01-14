package com.inso.plugin.act.datasheet;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.adapter.DataSheetBarAdapter;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.dao.StepDao;
import com.inso.plugin.model.DataSheetEntity;
import com.inso.plugin.tools.Configuration;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.NumUtil;
import com.inso.plugin.tools.TextStyle;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.view.AutoLocateHorizontalView;

import java.util.ArrayList;
import java.util.List;

import static com.inso.plugin.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inso.plugin.tools.MessUtil.getIntString;
import static com.inso.plugin.tools.TimeUtil.getMTS;

/**
 * Created by chendong on 2017/3/21.
 */
public class DataSheetAct extends BasicAct {
    private final int COLOR_RED = R.color.primaryColor;
    private final int COLOR_TITLE = R.color.black_60_transparent;
    private final int COLOR_CONTENT = R.color.black_90_transparent;
    private final int COLOR_UNIT = R.color.black_40_transparent;
    private final int SIZE_TITLE = 12;
    private final int SIZE_CONTENT = 28;
    private final int SIZE_UNIT = 12;
    private final int dayItemCount = 11;
    private final int weekItemCount = 5;
    private final int monItemCount = 5;
    private TextView[] tvArr = new TextView[4];
    private RadioButton[] rbArr = new RadioButton[3];
    private TextStyle mTs;
    private DataSheetBarAdapter adapter;
    private LinearLayout barContainer;
    private List<DataSheetEntity> dataSource = new ArrayList<>();
    private final int DAY_SEL = 0;
    private final int WEEK_SEL = 1;
    private final int MON_SEL = 2;
    private List<StepDao> mStepData = new ArrayList<>();
    private int selectTab = DAY_SEL;
    private String registerTime;
    private String[] dataSheetBar;
    private String[] enMonth;
    private String mTitle;


    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_datasheet;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        dataSheetBar = getResources().getStringArray(R.array.datasheet_bar);
        registerTime = TimeUtil.getRegisterTime((long) mDBHelper.getKeyTimeStamp(USER_REGISTER_KEY) * 1000,mDBHelper);
        L.e("registerTime:" + mDBHelper.getKeyTimeStamp(USER_REGISTER_KEY) + "," + registerTime);
        barContainer = (LinearLayout) findViewById(R.id.barContainer);
        tvArr[0] = (TextView) findViewById(R.id.tv_step);
        tvArr[1] = (TextView) findViewById(R.id.tv_act_duration);
        tvArr[2] = (TextView) findViewById(R.id.tv_distance);
        tvArr[3] = (TextView) findViewById(R.id.tv_consume);
        AccessbilityUtil.setAccessibilityFocusable(findViewById(R.id.title_bar_title),false);
        for (int i = 0; i < 4; i++) {
            AccessbilityUtil.setAccessibilityFocusable(tvArr[i],false);
        }
        rbArr[0] = (RadioButton) findViewById(R.id.tab01);
        rbArr[1] = (RadioButton) findViewById(R.id.tab02);
        rbArr[2] = (RadioButton) findViewById(R.id.tab03);

        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                for (int i = 0; i < 4; i++) {
                    tvArr[i].setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Normal.ttf"));
                }
            }

            @Override
            public void twHandle() {
                for (int i = 0; i < 4; i++) {
                    tvArr[i].setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Normal.ttf"));
                }
            }

            @Override
            public void hkHandle() {
                for (int i = 0; i < 4; i++) {
                    tvArr[i].setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Normal.ttf"));
                }
            }

            @Override
            public void enHandle() {

            }

            @Override
            public void defaultHandle() {

            }
        });
        for (int i = 0; i < 3; i++) {
            final int j = i;
            rbArr[j].setText(dataSheetBar[j]);
            rbArr[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectTab == j) return;
                    renderBarContainer(j);
                    selectTab = j;
                    setRbEnable(false);
                    barContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setRbEnable(true);
                        }
                    }, 500);
                }
            });
        }

        mTs = new TextStyle(ContextCompat.getColor(mContext, COLOR_TITLE), SIZE_TITLE);
        setGridData(0, 0, 0, 0);
        renderBarContainer(DAY_SEL);
    }

    /**
     * 防止测试妹子狂点
     *
     * @param b
     */
    private void setRbEnable(boolean b) {
        for (int i = 0; i < 3; i++) {
            rbArr[i].setEnabled(b);
        }
    }

    private void renderBarContainer(final int type) {
        if (barContainer.getChildCount() > 0)
            barContainer.removeAllViews();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        final AutoLocateHorizontalView horizontalView = new AutoLocateHorizontalView(mContext);
        switch (type) {
            case DAY_SEL:
                horizontalView.setItemCount(dayItemCount);
                break;
            case WEEK_SEL:
                horizontalView.setItemCount(weekItemCount);
                break;
            case MON_SEL:
                horizontalView.setItemCount(monItemCount);
                break;
        }
        fillInData(type);
        adapter = new DataSheetBarAdapter(this, dataSource);
        horizontalView.setInitPos(dataSource.size() - 1);
        horizontalView.setAdapter(adapter);
        horizontalView.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                try {
                    DataSheetEntity item = dataSource.get(pos);
                    setGridData(item.step, item.duration, item.distance, item.consume);
                    setDataSheetTitle(type, item);
//                    accessBarData = new AccessBarData(mTitle,tvArr);
//                    adapter.notifyAccessBar(accessBarData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        barContainer.addView(horizontalView, p);
        ViewTreeObserver viewTreeObserver = horizontalView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                horizontalView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                adapter.setMaxHeight(5 * horizontalView.getHeight() / 6);
            }
        });
    }

    /**
     * 根据选中项设置标题
     *
     * @param type
     * @param item
     */
    private void setDataSheetTitle(int type, final DataSheetEntity item) {
        switch (type) {
            case DAY_SEL:
                mTitle = item.year + getString(R.string.unit_year) + item.dateString.split("/")[0] + getString(R.string.unit_month) + item.dateString.split("/")[1] + getString(R.string.unit_day);
                Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                    @Override
                    public void cnHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void twHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void hkHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void enHandle() {
//                        enMonth = getResources().getStringArray(R.array.month);
                        int monIndex = Integer.parseInt(item.dateString.split("/")[0]);
                        int dayIndex = Integer.parseInt(item.dateString.split("/")[1]);
                        setTitleText(dayIndex + "\t" + enMonth[monIndex - 1] + getString(R.string.unit_year) + item.year);
                    }

                    @Override
                    public void defaultHandle() {
                        setTitleText(mTitle);
                    }
                });

                break;
            case WEEK_SEL:
                mTitle = item.year + getString(R.string.unit_year)
                        + item.week.split("-")[0].split("/")[0] + getString(R.string.unit_month)
                        + item.week.split("-")[0].split("/")[1] + getString(R.string.unit_day)
                        + "-"
                        + item.week.split("-")[1].split("/")[0] + getString(R.string.unit_month)
                        + item.week.split("-")[1].split("/")[1] + getString(R.string.unit_day);
                Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                    @Override
                    public void cnHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void twHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void hkHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void enHandle() {
//                        enMonth = getResources().getStringArray(R.array.month);
                        int fromMonIndex = Integer.parseInt(item.week.split("-")[0].split("/")[0]);
                        int fromDayIndex = Integer.parseInt(item.week.split("-")[0].split("/")[1]);
                        int toMonIndex = Integer.parseInt(item.week.split("-")[1].split("/")[0]);
                        int toDayIndex = Integer.parseInt(item.week.split("-")[1].split("/")[1]);
                        setTitleText(fromDayIndex + "\t" + enMonth[fromMonIndex - 1] + " - " + toDayIndex + "\t" + enMonth[toMonIndex - 1]);
                    }

                    @Override
                    public void defaultHandle() {
                        setTitleText(mTitle);
                    }
                });
                break;
            case MON_SEL:
                mTitle =  item.year + getString(R.string.unit_year) + item.dateString;
                Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                    @Override
                    public void cnHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void twHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void hkHandle() {
                        setTitleText(mTitle);
                    }

                    @Override
                    public void enHandle() {
                        setTitleText(item.dateString + getString(R.string.unit_year) + item.year);
                    }

                    @Override
                    public void defaultHandle() {
                        setTitleText(mTitle);
                    }
                });
                break;
        }
    }

    /**
     * 根据选中类型填充数据
     *
     * @param type
     */
    private void fillInData(int type) {
        dataSource = new ArrayList<>();
        mStepData = new ArrayList<>();
        TimeUtil.TimeCompare cmp = getMTS(mDBHelper.getSettingZone());
        if (cmp == TimeUtil.TimeCompare.LESS) {
            TimeUtil.initZone(mDBHelper);
        } else if (cmp == TimeUtil.TimeCompare.MORE) {
            TimeUtil.initZone(mDBHelper);
        } else if ((cmp == TimeUtil.TimeCompare.EQUAL) ) {
            TimeUtil.releaseZone();
        }
        switch (type) {
            case DAY_SEL:
                mStepData = mDBHelper.getStepDayData();
                for (StepDao dao : mStepData) {
                    L.e(dao.toString());
                }
                for (TimeUtil.DataSheetTime item : TimeUtil.getUiDays(registerTime, mDBHelper)) {
                    L.e(item.toString());
                    dataSource.add(new DataSheetEntity(item.year, item.uidate));
                }
                for (StepDao dao : mStepData) {
                    for (DataSheetEntity item : dataSource) {
                        if (TextUtils.equals(dao.year, item.year) && TextUtils.equals(dao.day, item.dateString)) {
                            item.step = dao.step;
                            item.distance = dao.distance;
                            item.consume = dao.consume;
                            item.duration = dao.duration;
                        }
                    }
                }
                break;
            case WEEK_SEL:
                mStepData = mDBHelper.getStepWeekData();
                for (TimeUtil.DataSheetTime item : TimeUtil.getUiWeek(registerTime)) {
                    dataSource.add(new DataSheetEntity(item.year, getWeekString(item.weekOfYear), item.uidate));
                }
                for (StepDao dao : mStepData) {
                    for (DataSheetEntity item : dataSource) {
                        if (TextUtils.equals(dao.year, item.year) && TextUtils.equals(dao.week, item.week)) {
                            item.step = dao.step;
                            item.distance = dao.distance;
                            item.consume = dao.consume;
                            item.duration = dao.duration;
                        }
                    }
                }
                break;
            case MON_SEL:
                mStepData = mDBHelper.getStepMonData();
//                for (StepDao item : mStepData) {
//                    L.e("item:" + item.toString());
//                }
                for (final TimeUtil.DataSheetTime item : TimeUtil.getUiMon(registerTime)) {
                    Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                        @Override
                        public void cnHandle() {
                            item.uidate += getString(R.string.unit_month);
                        }

                        @Override
                        public void twHandle() {
                            item.uidate += getString(R.string.unit_month);
                        }

                        @Override
                        public void hkHandle() {
                            item.uidate += getString(R.string.unit_month);
                        }

                        @Override
                        public void enHandle() {
                            try {
//                                enMonth = getResources().getStringArray(R.array.month);
                                item.uidate = enMonth[Integer.parseInt(item.uidate) - 1];
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void defaultHandle() {
                            item.uidate += getString(R.string.unit_month);
                        }
                    });
                    dataSource.add(new DataSheetEntity(item.year, item.uidate));
                }
//                for (DataSheetEntity item : dataSource) {
//                    L.e("Entity:" + item.toString());
//                }
                Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                    @Override
                    public void cnHandle() {
                        createData();
                    }

                    @Override
                    public void twHandle() {
                        createData();
                    }

                    @Override
                    public void hkHandle() {
                        createData();
                    }

                    @Override
                    public void enHandle() {
                        for (StepDao dao : mStepData) {
                            for (final DataSheetEntity item : dataSource) {
                                if (TextUtils.equals(dao.year, item.year) && TextUtils.equals(enMonth[Integer.parseInt(dao.mon) - 1], item.dateString)) {
                                    item.step = dao.step;
                                    item.distance = dao.distance;
                                    item.consume = dao.consume;
                                    item.duration = dao.duration;
                                }
                            }
                        }
                    }

                    @Override
                    public void defaultHandle() {
                        createData();
                    }

                    private void createData() {
                        for (StepDao dao : mStepData) {
                            for (final DataSheetEntity item : dataSource) {
                                if (TextUtils.equals(dao.year, item.year) && TextUtils.equals(dao.mon, getIntString(item.dateString))) {
                                    item.step = dao.step;
                                    item.distance = dao.distance;
                                    item.consume = dao.consume;
                                    item.duration = dao.duration;
                                }
                            }
                        }
                    }

                });
                break;
        }
    }

    private String getWeekString(int weekOfYear) {
        if (weekOfYear == 1) {
            return "1rst";
        } else if (weekOfYear == 2) {
            return "2nd";
        } else if (weekOfYear == 3) {
            return "3rd";
        } else {
            return weekOfYear + "th";
        }
    }

    /**
     * @param step     b
     * @param seconds  s
     * @param distance m
     * @param consume  kc
     */
    private void setGridData(int step, int seconds, int distance, int consume) {
        String[] source = null;
        switch (selectTab) {
            case DAY_SEL:
                source = getResources().getStringArray(R.array.sheet_grid_day);
                break;
            case WEEK_SEL:
                source = getResources().getStringArray(R.array.sheet_grid_week);
                break;
            case MON_SEL:
                source = getResources().getStringArray(R.array.sheet_grid_mon);
                break;
        }
        if (null != source) {
            tvArr[0].setText(mTs.clear()
                    .setColor(ContextCompat.getColor(mContext, COLOR_TITLE))
                    .setSize(SIZE_TITLE)
                    .spanColorAndSize(source[0])
                    .setColor(ContextCompat.getColor(mContext, COLOR_RED))
                    .setSize(SIZE_CONTENT)
                    .spanColorAndSize(String.valueOf(step))
                    .setColor(ContextCompat.getColor(mContext, COLOR_UNIT))
                    .setSize(SIZE_UNIT)
                    .spanColorAndSize(getString(R.string.unit_step))
                    .getText());
            tvArr[1].setText(mTs.clear()
                    .setColor(ContextCompat.getColor(mContext, COLOR_TITLE))
                    .setSize(SIZE_TITLE)
                    .spanColorAndSize(source[1])
                    .setColor(ContextCompat.getColor(mContext, COLOR_CONTENT))
                    .setSize(SIZE_CONTENT)
                    .spanColorAndSize(String.valueOf(seconds / 3600))
                    .setColor(ContextCompat.getColor(mContext, COLOR_UNIT))
                    .setSize(SIZE_UNIT)
                    .spanColorAndSize(getString(R.string.unit_hour))
                    .setColor(ContextCompat.getColor(mContext, COLOR_CONTENT))
                    .setSize(SIZE_CONTENT)
                    .spanColorAndSize(TextUtils.concat("\t", String.valueOf(NumUtil.getRestMin(seconds))).toString())
                    .setColor(ContextCompat.getColor(mContext, COLOR_UNIT))
                    .setSize(SIZE_UNIT)
                    .spanColorAndSize(getString(R.string.unit_min))
                    .getText());
            tvArr[2].setText(mTs.clear()
                    .setColor(ContextCompat.getColor(mContext, COLOR_TITLE))
                    .setSize(SIZE_TITLE)
                    .spanColorAndSize(source[2])
                    .setColor(ContextCompat.getColor(mContext, COLOR_CONTENT))
                    .setSize(SIZE_CONTENT)
                    .spanColorAndSize(NumUtil.doubleNumRestOne((double) distance / 1000))
                    .setColor(ContextCompat.getColor(mContext, COLOR_UNIT))
                    .setSize(SIZE_UNIT)
                    .spanColorAndSize(getString(R.string.unit_km))
                    .getText());
            tvArr[3].setText(mTs.clear()
                    .setColor(ContextCompat.getColor(mContext, COLOR_TITLE))
                    .setSize(SIZE_TITLE)
                    .spanColorAndSize(source[3])
                    .setColor(ContextCompat.getColor(mContext, COLOR_CONTENT))
                    .setSize(SIZE_CONTENT)
                    .spanColorAndSize(String.valueOf(consume))
                    .setColor(ContextCompat.getColor(mContext, COLOR_UNIT))
                    .setSize(SIZE_UNIT)
                    .spanColorAndSize(getString(R.string.unit_kc))
                    .getText());
        }
    }
}
