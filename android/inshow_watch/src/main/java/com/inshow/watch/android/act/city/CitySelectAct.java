package com.inshow.watch.android.act.city;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.adapter.CityAdapter;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.fragment.CitySearchFragment;
import com.inshow.watch.android.fragment.ICheckOnClick;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.model.CityEntity;
import com.inshow.watch.android.sync.http.bean.HttpCityRes;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.indexable.EntityWrapper;
import com.inshow.watch.android.view.indexable.IndexableAdapter;
import com.inshow.watch.android.view.indexable.IndexableLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.inshow.watch.android.tools.Constants.ConfigVersion.WORLD_CITY;

/**
 * Created by chendong on 2017/1/22.
 * 选择城市
 */
public class CitySelectAct extends BasicAct {
    private List<CityEntity> mDataSrc;
    private FrameLayout mProgressBar;
    private EditText searchEt;
    private IndexableLayout indexableLayout;
    private FrameLayout flList;
    private Button buttonOK;
    private TextView selectAllTitle;
    private CityAdapter mAdapter;
    private List<PreferCitiesDao> dataSource = new ArrayList<>();
    private HashSet<Long> sets = new HashSet<>();
    private CitySearchFragment mSearchFragment;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_cityselect;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress(R.id.select_all_cancel);
        setActStyle(ActStyle.BT);
        dataSource = mDBHelper.getAllPreferCities();
        flList = (FrameLayout) findViewById(R.id.fl_list);
        mSearchFragment = CitySearchFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.city_search_fragment, mSearchFragment)
                .commitAllowingStateLoss();
        mSearchFragment.setListener(new ICheckOnClick<CityEntity>() {
            @Override
            public void ifItemOriginChecked(CityEntity cityEntity) {
                removeSet(cityEntity);
            }

            @Override
            public boolean onClickedAllowed() {
                addSets(mSearchFragment.mSelDatas);
                addSets(mDataSrc);
                if (sets.size() + mDBHelper.getAllPreferCities().size() > 9) {
                    ToastUtil.showToastNoRepeat(mContext, getString(R.string.city_most_tip));
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onItemClick(CityEntity cityEntity) {

            }
        });
        indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
        searchEt = (EditText) findViewById(R.id.et_search);
        mProgressBar = (FrameLayout) findViewById(R.id.progress);
        mTitleView.setVisibility(View.GONE);
        flSelectAll.setVisibility(View.VISIBLE);
        buttonOK = (Button) findViewById(R.id.select_all_select);
        buttonOK.setText(R.string.button_ok);
        selectAllTitle = (TextView) findViewById(R.id.select_all_title);
        selectAllTitle.setText(R.string.please_select_city);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(searchEt.getText())) {
                    mDBHelper.addPreferCity(getAddSrc());
                    finish();
                } else {//search frg
                    searchEt.setText("");
                    searchEt.clearFocus();
                    if (mSearchFragment.mSelDatas.size() > 0) {
                        for (CityEntity selitem : mSearchFragment.mSelDatas) {
                            for (CityEntity srcitem : mDataSrc) {
                                if (selitem.getId() == srcitem.getId()) {
                                    srcitem.status = selitem.status;
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        indexableLayout.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new CityAdapter(this);
        indexableLayout.setAdapter(mAdapter);
        mDataSrc = initDatas();
        // 快速排序。  排序规则设置为：只按首字母  （默认全拼音排序）  效率很高，是默认的10倍左右。  按需开启～
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);
        mAdapter.setDatas(mDataSrc, new IndexableAdapter.IndexCallback<CityEntity>() {
            @Override
            public void onFinished(List<EntityWrapper<CityEntity>> datas) {
                // 数据处理完成后回调
                mSearchFragment.bindDatas(mDataSrc);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        indexableLayout.setOverlayStyle_Center();
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
                if (!entity.status && getAddSrc().size() + mDBHelper.getAllPreferCities().size() > 9) { //原来就是非选中状态
                    ToastUtil.showToastNoRepeat(mContext, getString(R.string.city_most_tip));
                    return;
                }else if(entity.status){//原来就是选中状态
                    removeSet(entity);
                }
                entity.status = !entity.status;
                mAdapter.notifyDataSetChanged();
            }
        });
        initSearch();
    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
//        HttpCityRes bean = AppController.getGson().fromJson(FileUtil.ReadFile(mContext,FileUtil.getCityFilePath()),HttpCityRes.class);
        HttpCityRes bean = AppController.getGson().fromJson(mDBHelper.getCache(WORLD_CITY), HttpCityRes.class);
        if(bean!=null) {
            for (HttpCityRes.CityListBean item : bean.getCity_list()) {
                CityEntity cityEntity = new CityEntity(mContext);
                cityEntity.setId(Long.parseLong(item.getId()));
                cityEntity.setZone(item.getZone());
                cityEntity.status = false;
                cityEntity.zh = item.getZh_cn();
                cityEntity.en = item.getEn();
                cityEntity.zh_hk = item.getZh_hk();
                cityEntity.zh_tw = item.getZh_tw();
                list.add(cityEntity);
            }
        }
        List<CityEntity> source = new ArrayList<>();
        for (PreferCitiesDao dao : dataSource) {
            CityEntity cityEntity = new CityEntity(mContext);
            cityEntity.setId(dao.id);
            cityEntity.setZone(dao.zone);
            cityEntity.status = false;
            cityEntity.zh = dao.zh_cn;
            cityEntity.en = dao.en;
            cityEntity.zh_hk = dao.zh_hk;
            cityEntity.zh_tw = dao.zh_tw;
            source.add(cityEntity);
        }
        list.removeAll(source);
        return list;
    }

    /**
     * 获得要添加的城市
     * @return
     */
    @Nullable
    private List<PreferCitiesDao> getAddSrc() {
        List<PreferCitiesDao> addSrc = new ArrayList<>();
        for (CityEntity item : mDataSrc) {
            if (item.status) {
                addSrc.add(new PreferCitiesDao(item.getId(), item.zh, item.en, item.zh_tw, item.zh_hk, item.getZone(), false, false));
            }
        }
        return addSrc;
    }

    private void addSets(List<CityEntity> src){
        for (CityEntity item : src) {
            if(item.status){
                sets.add(item.getId());
            }
        }
    }
    private void removeSet(CityEntity item){
        sets.remove(item.getId());
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonOK.setText(TextUtils.isEmpty(searchEt.getText().toString()) ? getString(R.string.button_ok) : getString(R.string.btn_complete));
                String newText = searchEt.getText().toString();
                flList.setVisibility(TextUtils.isEmpty(newText) ? View.VISIBLE : View.GONE);
                mSearchFragment.bindQueryText(newText);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchEt.isFocused()) {
            searchEt.setText("");
            searchEt.clearFocus();
            return;
        }
        super.onBackPressed();
    }
}
