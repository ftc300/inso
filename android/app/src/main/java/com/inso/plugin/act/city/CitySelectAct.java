package com.inso.plugin.act.city;

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

import com.inso.R;
import com.inso.plugin.adapter.CityAdapter;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.dao.PreferCitiesDao;
import com.inso.plugin.fragment.CitySearchFragment;
import com.inso.plugin.fragment.ICheckOnClick;
import com.inso.plugin.manager.AppController;
import com.inso.plugin.model.CityEntity;
import com.inso.plugin.sync.http.bean.HttpCityRes;
import com.inso.plugin.tools.ToastUtil;
import com.inso.plugin.view.indexable.EntityWrapper;
import com.inso.plugin.view.indexable.IndexableAdapter;
import com.inso.plugin.view.indexable.IndexableLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


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
        String temp = "{\"version\":\"15\",\"city_list\":[{\"id\":\"1\",\"zone\":\"Asia/Shanghai\",\"en\":\"Beijing\",\"zh_cn\":\"北京\",\"zh_hk\":\"北京\",\"zh_tw\":\"北京\"},{\"id\":\"2\",\"zone\":\"Africa/Abidjan\",\"en\":\"Abidjan\",\"zh_cn\":\"阿比让\",\"zh_hk\":\"阿比讓\",\"zh_tw\":\"阿必尚\"},{\"id\":\"3\",\"zone\":\"Africa/Accra\",\"en\":\"Accra\",\"zh_cn\":\"阿克拉\",\"zh_hk\":\"阿克拉\",\"zh_tw\":\"阿克拉\"},{\"id\":\"4\",\"zone\":\"Africa/Addis_Ababa\",\"en\":\"Addis Ababa\",\"zh_cn\":\"亚的斯亚贝巴\",\"zh_hk\":\"阿迪斯阿貝巴\",\"zh_tw\":\"阿迪斯阿貝巴\"},{\"id\":\"5\",\"zone\":\"Australia/Adelaide\",\"en\":\"Adelaide\",\"zh_cn\":\"阿德莱德\",\"zh_hk\":\"阿得萊德\",\"zh_tw\":\"阿得雷德\"},{\"id\":\"6\",\"zone\":\"America/Denver\",\"en\":\"Albuquerque\",\"zh_cn\":\"阿尔伯克基\",\"zh_hk\":\"阿爾伯克基\",\"zh_tw\":\"阿布奎基\"},{\"id\":\"7\",\"zone\":\"Africa/Algiers\",\"en\":\"Algiers\",\"zh_cn\":\"阿尔及尔\",\"zh_hk\":\"阿爾及爾\",\"zh_tw\":\"阿爾及爾\"},{\"id\":\"8\",\"zone\":\"Asia/Almaty\",\"en\":\"Almaty\",\"zh_cn\":\"阿拉木图\",\"zh_hk\":\"阿拉木圖\",\"zh_tw\":\"阿拉木圖\"},{\"id\":\"9\",\"zone\":\"Asia/Amman\",\"en\":\"Amman\",\"zh_cn\":\"安曼\",\"zh_hk\":\"安曼\",\"zh_tw\":\"安曼\"},{\"id\":\"10\",\"zone\":\"Europe/Amsterdam\",\"en\":\"Amsterdam\",\"zh_cn\":\"阿姆斯特丹\",\"zh_hk\":\"阿姆斯特丹\",\"zh_tw\":\"阿姆斯特丹\"},{\"id\":\"11\",\"zone\":\"Asia/Anadyr\",\"en\":\"Anadyr\",\"zh_cn\":\"阿纳德尔\",\"zh_hk\":\"阿納德爾\",\"zh_tw\":\"阿納德爾\"},{\"id\":\"12\",\"zone\":\"America/Anchorage\",\"en\":\"Anchorage\",\"zh_cn\":\"安克雷奇\",\"zh_hk\":\"安克雷奇\",\"zh_tw\":\"安克拉治\"},{\"id\":\"13\",\"zone\":\"Europe/Andorra\",\"en\":\"Andorra\",\"zh_cn\":\"安道尔\",\"zh_hk\":\"安道爾\",\"zh_tw\":\"安道爾\"},{\"id\":\"14\",\"zone\":\"Europe/Istanbul\",\"en\":\"Ankara\",\"zh_cn\":\"安卡拉\",\"zh_hk\":\"安卡拉\",\"zh_tw\":\"安卡拉\"},{\"id\":\"15\",\"zone\":\"America/Detroit\",\"en\":\"Ann Arbor\",\"zh_cn\":\"安阿伯\",\"zh_hk\":\"安娜堡\",\"zh_tw\":\"安娜堡\"},{\"id\":\"16\",\"zone\":\"Indian/Antananarivo\",\"en\":\"Antananarivo\",\"zh_cn\":\"安塔那利佛\",\"zh_hk\":\"安塔那那利佛\",\"zh_tw\":\"安塔那那利佛\"},{\"id\":\"17\",\"zone\":\"America/Antigua\",\"en\":\"Antigua\",\"zh_cn\":\"安提瓜\",\"zh_hk\":\"聖約翰斯\",\"zh_tw\":\"安地瓜\"},{\"id\":\"18\",\"zone\":\"Asia/Aqtau\",\"en\":\"Aqtau\",\"zh_cn\":\"阿克套\",\"zh_hk\":\"阿克套\",\"zh_tw\":\"阿克套\"},{\"id\":\"19\",\"zone\":\"America/Aruba\",\"en\":\"Aruba\",\"zh_cn\":\"阿鲁巴\",\"zh_hk\":\"阿魯巴島\",\"zh_tw\":\"阿魯巴\"},{\"id\":\"20\",\"zone\":\"America/Asuncion\",\"en\":\"Asunción\",\"zh_cn\":\"亚松森\",\"zh_hk\":\"阿松森\",\"zh_tw\":\"亞松森\"},{\"id\":\"21\",\"zone\":\"Europe/Athens\",\"en\":\"Athens\",\"zh_cn\":\"雅典\",\"zh_hk\":\"雅典\",\"zh_tw\":\"雅典\"},{\"id\":\"22\",\"zone\":\"America/New_York\",\"en\":\"Atlanta\",\"zh_cn\":\"亚特兰大\",\"zh_hk\":\"亞特蘭大\",\"zh_tw\":\"亞特蘭大\"},{\"id\":\"23\",\"zone\":\"Pacific/Auckland\",\"en\":\"Auckland\",\"zh_cn\":\"奥克兰\",\"zh_hk\":\"奧克蘭\",\"zh_tw\":\"奧克蘭\"},{\"id\":\"24\",\"zone\":\"America/Chicago\",\"en\":\"Austin\",\"zh_cn\":\"奥斯汀\",\"zh_hk\":\"奧斯丁\",\"zh_tw\":\"奧斯丁\"},{\"id\":\"25\",\"zone\":\"Asia/Baghdad\",\"en\":\"Baghdad\",\"zh_cn\":\"巴格达\",\"zh_hk\":\"巴格達\",\"zh_tw\":\"巴格達\"},{\"id\":\"26\",\"zone\":\"Asia/Bahrain\",\"en\":\"Bahrain\",\"zh_cn\":\"巴林\",\"zh_hk\":\"巴林\",\"zh_tw\":\"巴林\"},{\"id\":\"27\",\"zone\":\"Asia/Baku\",\"en\":\"Baku\",\"zh_cn\":\"巴库\",\"zh_hk\":\"巴庫\",\"zh_tw\":\"巴庫\"},{\"id\":\"28\",\"zone\":\"America/New_York\",\"en\":\"Baltimore\",\"zh_cn\":\"巴尔的摩\",\"zh_hk\":\"巴爾的摩\",\"zh_tw\":\"巴爾的摩\"},{\"id\":\"29\",\"zone\":\"Asia/Kolkata\",\"en\":\"Bangalore\",\"zh_cn\":\"班加罗尔\",\"zh_hk\":\"班加羅爾\",\"zh_tw\":\"班加羅爾\"},{\"id\":\"30\",\"zone\":\"Asia/Bangkok\",\"en\":\"Bangkok\",\"zh_cn\":\"曼谷\",\"zh_hk\":\"曼谷\",\"zh_tw\":\"曼谷\"},{\"id\":\"31\",\"zone\":\"America/Barbados\",\"en\":\"Barbados\",\"zh_cn\":\"巴巴多斯\",\"zh_hk\":\"巴巴多斯\",\"zh_tw\":\"巴貝多\"},{\"id\":\"32\",\"zone\":\"Europe/Madrid\",\"en\":\"Barcelona\",\"zh_cn\":\"巴塞罗纳\",\"zh_hk\":\"巴塞隆拿\",\"zh_tw\":\"巴塞隆納\"},{\"id\":\"33\",\"zone\":\"Asia/Beirut\",\"en\":\"Beirut\",\"zh_cn\":\"贝鲁特\",\"zh_hk\":\"貝魯特\",\"zh_tw\":\"貝魯特\"},{\"id\":\"34\",\"zone\":\"Europe/Belfast\",\"en\":\"Belfast\",\"zh_cn\":\"贝尔法斯特\",\"zh_hk\":\"貝爾法斯特\",\"zh_tw\":\"貝爾法斯特\"},{\"id\":\"35\",\"zone\":\"Europe/Belgrade\",\"en\":\"Belgrade\",\"zh_cn\":\"贝尔格莱德\",\"zh_hk\":\"貝爾格來德\",\"zh_tw\":\"貝爾格勒\"},{\"id\":\"36\",\"zone\":\"America/Belize\",\"en\":\"Belize\",\"zh_cn\":\"伯利兹\",\"zh_hk\":\"貝里斯\",\"zh_tw\":\"貝里斯\"},{\"id\":\"37\",\"zone\":\"America/Sao_Paulo\",\"en\":\"Belo Horizonte\",\"zh_cn\":\"贝洛奥里藏特\",\"zh_hk\":\"貝洛奧里藏特\",\"zh_tw\":\"美景市\"},{\"id\":\"38\",\"zone\":\"Europe/Berlin\",\"en\":\"Berlin\",\"zh_cn\":\"柏林\",\"zh_hk\":\"柏林\",\"zh_tw\":\"柏林\"},{\"id\":\"39\",\"zone\":\"Atlantic/Bermuda\",\"en\":\"Bermuda\",\"zh_cn\":\"百慕大\",\"zh_hk\":\"百慕達\",\"zh_tw\":\"百慕達\"},{\"id\":\"40\",\"zone\":\"America/North_Dakota/Beulah\",\"en\":\"Beulah\",\"zh_cn\":\"比尤拉\",\"zh_hk\":\"比尤拉\",\"zh_tw\":\"比尤拉\"},{\"id\":\"41\",\"zone\":\"Africa/Blantyre\",\"en\":\"Blantyre\",\"zh_cn\":\"布兰太尔\",\"zh_hk\":\"布蘭太爾\",\"zh_tw\":\"布蘭泰爾\"},{\"id\":\"42\",\"zone\":\"America/Bogota\",\"en\":\"Bogotá\",\"zh_cn\":\"波哥大\",\"zh_hk\":\"波哥大\",\"zh_tw\":\"波哥大\"},{\"id\":\"43\",\"zone\":\"America/New_York\",\"en\":\"Boston\",\"zh_cn\":\"波士顿\",\"zh_hk\":\"波士頓\",\"zh_tw\":\"波士頓\"},{\"id\":\"44\",\"zone\":\"America/Denver\",\"en\":\"Boulder\",\"zh_cn\":\"博尔德\",\"zh_hk\":\"波德戈里察\",\"zh_tw\":\"波德\"},{\"id\":\"45\",\"zone\":\"America/Sao_Paulo\",\"en\":\"Brasília\",\"zh_cn\":\"巴西利亚\",\"zh_hk\":\"巴西利亞\",\"zh_tw\":\"巴西利亞\"},{\"id\":\"46\",\"zone\":\"Europe/Bratislava\",\"en\":\"Bratislava\",\"zh_cn\":\"布拉迪斯拉发\",\"zh_hk\":\"布拉提斯拉發\",\"zh_tw\":\"布拉提斯拉瓦\"},{\"id\":\"47\",\"zone\":\"Africa/Brazzaville\",\"en\":\"Brazzaville\",\"zh_cn\":\"布拉柴维尔\",\"zh_hk\":\"布拉柴維爾\",\"zh_tw\":\"布拉薩\"},{\"id\":\"48\",\"zone\":\"Australia/Brisbane\",\"en\":\"Brisbane\",\"zh_cn\":\"布里斯班\",\"zh_hk\":\"布里斯本\",\"zh_tw\":\"布里斯本\"},{\"id\":\"49\",\"zone\":\"Europe/Brussels\",\"en\":\"Brussels\",\"zh_cn\":\"布鲁塞尔\",\"zh_hk\":\"布魯塞爾\",\"zh_tw\":\"布魯塞爾\"},{\"id\":\"50\",\"zone\":\"Europe/Bucharest\",\"en\":\"Bucharest\",\"zh_cn\":\"布加勒斯特\",\"zh_hk\":\"布加勒斯特\",\"zh_tw\":\"布加勒斯特\"},{\"id\":\"51\",\"zone\":\"Europe/Budapest\",\"en\":\"Budapest\",\"zh_cn\":\"布达佩斯\",\"zh_hk\":\"布達佩斯\",\"zh_tw\":\"布達佩斯\"},{\"id\":\"52\",\"zone\":\"America/Argentina/Buenos_Aires\",\"en\":\"Buenos Aires\",\"zh_cn\":\"布宜诺斯艾利斯\",\"zh_hk\":\"布宜諾斯艾利斯\",\"zh_tw\":\"布宜諾斯艾利斯\"},{\"id\":\"53\",\"zone\":\"Africa/Cairo\",\"en\":\"Cairo\",\"zh_cn\":\"开罗\",\"zh_hk\":\"開羅\",\"zh_tw\":\"開羅\"},{\"id\":\"54\",\"zone\":\"Asia/Calcutta\",\"en\":\"Calcutta\",\"zh_cn\":\"加尔各答\",\"zh_hk\":\"加爾各答\",\"zh_tw\":\"加爾各答\"},{\"id\":\"55\",\"zone\":\"America/Edmonton\",\"en\":\"Calgary\",\"zh_cn\":\"卡尔加里\",\"zh_hk\":\"卡加利\",\"zh_tw\":\"卡加利\"},{\"id\":\"56\",\"zone\":\"America/New_York\",\"en\":\"Cambridge\",\"zh_cn\":\"剑桥\",\"zh_hk\":\"劍橋\",\"zh_tw\":\"劍橋\"},{\"id\":\"57\",\"zone\":\"Atlantic/Canary\",\"en\":\"Canary Islands\",\"zh_cn\":\"加那利群岛\",\"zh_hk\":\"加那利群島\",\"zh_tw\":\"加那利群島\"},{\"id\":\"58\",\"zone\":\"Australia/Canberra\",\"en\":\"Canberra\",\"zh_cn\":\"堪培拉\",\"zh_hk\":\"坎培拉\",\"zh_tw\":\"坎培拉\"},{\"id\":\"59\",\"zone\":\"America/Cancun\",\"en\":\"Cancun\",\"zh_cn\":\"坎昆\",\"zh_hk\":\"坎昆\",\"zh_tw\":\"坎昆\"},{\"id\":\"60\",\"zone\":\"Africa/Johannesburg\",\"en\":\"Cape Town\",\"zh_cn\":\"开普敦\",\"zh_hk\":\"開普敦\",\"zh_tw\":\"開普敦\"},{\"id\":\"61\",\"zone\":\"Atlantic/Cape_Verde\",\"en\":\"Cape Verde\",\"zh_cn\":\"佛得角\",\"zh_hk\":\"佛得角\",\"zh_tw\":\"維德角\"},{\"id\":\"62\",\"zone\":\"America/Caracas\",\"en\":\"Caracas\",\"zh_cn\":\"加拉加斯\",\"zh_hk\":\"加拉加斯\",\"zh_tw\":\"卡拉卡斯\"},{\"id\":\"63\",\"zone\":\"Africa/Casablanca\",\"en\":\"Casablanca\",\"zh_cn\":\"卡萨布兰卡\",\"zh_hk\":\"卡薩布蘭卡\",\"zh_tw\":\"卡薩布蘭卡\"},{\"id\":\"64\",\"zone\":\"America/Cayman\",\"en\":\"Cayman Islands\",\"zh_cn\":\"开曼群岛\",\"zh_hk\":\"開曼群島\",\"zh_tw\":\"開曼群島\"},{\"id\":\"65\",\"zone\":\"America/Chicago\",\"en\":\"Chicago\",\"zh_cn\":\"芝加哥\",\"zh_hk\":\"芝加哥\",\"zh_tw\":\"芝加哥\"},{\"id\":\"66\",\"zone\":\"America/Chihuahua\",\"en\":\"Chihuahua\",\"zh_cn\":\"奇瓦瓦\",\"zh_hk\":\"芝娃娃\",\"zh_tw\":\"契瓦瓦\"},{\"id\":\"67\",\"zone\":\"Europe/Chisinau\",\"en\":\"Chisinau\",\"zh_cn\":\"基希讷乌\",\"zh_hk\":\"基希納烏\",\"zh_tw\":\"奇西瑙\"},{\"id\":\"68\",\"zone\":\"America/New_York\",\"en\":\"Cincinnati\",\"zh_cn\":\"辛辛那提\",\"zh_hk\":\"辛辛那提\",\"zh_tw\":\"辛辛那提\"},{\"id\":\"69\",\"zone\":\"America/New_York\",\"en\":\"Cleveland\",\"zh_cn\":\"克利夫兰\",\"zh_hk\":\"克里夫蘭\",\"zh_tw\":\"克里夫蘭\"},{\"id\":\"70\",\"zone\":\"Asia/Colombo\",\"en\":\"Colombo\",\"zh_cn\":\"科伦坡\",\"zh_hk\":\"科倫坡\",\"zh_tw\":\"可倫坡\"},{\"id\":\"71\",\"zone\":\"America/New_York\",\"en\":\"Columbus\",\"zh_cn\":\"哥伦布\",\"zh_hk\":\"哥倫布\",\"zh_tw\":\"哥倫布\"},{\"id\":\"72\",\"zone\":\"Africa/Conakry\",\"en\":\"Conakry\",\"zh_cn\":\"科纳克里\",\"zh_hk\":\"科納克里\",\"zh_tw\":\"柯那克里\"},{\"id\":\"73\",\"zone\":\"Europe/Copenhagen\",\"en\":\"Copenhagen\",\"zh_cn\":\"哥本哈根\",\"zh_hk\":\"哥本哈根\",\"zh_tw\":\"哥本哈根\"},{\"id\":\"74\",\"zone\":\"America/Costa_Rica\",\"en\":\"Costa Rica\",\"zh_cn\":\"哥斯达黎加\",\"zh_hk\":\"哥斯達黎加\",\"zh_tw\":\"哥斯大黎加\"},{\"id\":\"75\",\"zone\":\"America/Curacao\",\"en\":\"Curacao\",\"zh_cn\":\"库拉索\",\"zh_hk\":\"庫拉索\",\"zh_tw\":\"古拉索\"},{\"id\":\"76\",\"zone\":\"Africa/Dakar\",\"en\":\"Dakar\",\"zh_cn\":\"达喀尔\",\"zh_hk\":\"達喀爾\",\"zh_tw\":\"達喀爾\"},{\"id\":\"77\",\"zone\":\"America/Chicago\",\"en\":\"Dallas\",\"zh_cn\":\"达拉斯\",\"zh_hk\":\"達拉斯\",\"zh_tw\":\"達拉斯\"},{\"id\":\"78\",\"zone\":\"Asia/Damascus\",\"en\":\"Damascus\",\"zh_cn\":\"大马士革\",\"zh_hk\":\"大馬士革\",\"zh_tw\":\"大馬士革\"},{\"id\":\"79\",\"zone\":\"Africa/Dar_es_Salaam\",\"en\":\"Dar es Salaam\",\"zh_cn\":\"达累斯萨拉姆\",\"zh_hk\":\"達累斯薩拉姆\",\"zh_tw\":\"沙蘭港\"},{\"id\":\"80\",\"zone\":\"Australia/Darwin\",\"en\":\"Darwin\",\"zh_cn\":\"达尔文\",\"zh_hk\":\"達爾文\",\"zh_tw\":\"達爾文\"},{\"id\":\"81\",\"zone\":\"America/Dawson_Creek\",\"en\":\"Dawson Creek\",\"zh_cn\":\"道森克里克\",\"zh_hk\":\"道森灣\",\"zh_tw\":\"道森河\"},{\"id\":\"82\",\"zone\":\"Asia/Kolkata\",\"en\":\"Delhi\",\"zh_cn\":\"德里\",\"zh_hk\":\"德里\",\"zh_tw\":\"德里\"},{\"id\":\"83\",\"zone\":\"America/Denver\",\"en\":\"Denver\",\"zh_cn\":\"丹佛\",\"zh_hk\":\"丹佛\",\"zh_tw\":\"丹佛\"},{\"id\":\"84\",\"zone\":\"America/Detroit\",\"en\":\"Detroit\",\"zh_cn\":\"底特律\",\"zh_hk\":\"底特律\",\"zh_tw\":\"底特律\"},{\"id\":\"85\",\"zone\":\"Asia/Dhaka\",\"en\":\"Dhaka\",\"zh_cn\":\"达卡\",\"zh_hk\":\"達卡\",\"zh_tw\":\"達卡\"},{\"id\":\"86\",\"zone\":\"Africa/Djibouti\",\"en\":\"Djibouti\",\"zh_cn\":\"吉布提\",\"zh_hk\":\"吉布提\",\"zh_tw\":\"吉布地\"},{\"id\":\"87\",\"zone\":\"Asia/Qatar\",\"en\":\"Doha\",\"zh_cn\":\"多哈\",\"zh_hk\":\"多哈\",\"zh_tw\":\"杜哈\"},{\"id\":\"88\",\"zone\":\"America/Dominica\",\"en\":\"Dominica\",\"zh_cn\":\"多米尼加\",\"zh_hk\":\"多米尼克\",\"zh_tw\":\"多明尼加\"},{\"id\":\"89\",\"zone\":\"Asia/Dubai\",\"en\":\"Dubai\",\"zh_cn\":\"迪拜\",\"zh_hk\":\"杜拜\",\"zh_tw\":\"杜拜\"},{\"id\":\"90\",\"zone\":\"Europe/Dublin\",\"en\":\"Dublin\",\"zh_cn\":\"都柏林\",\"zh_hk\":\"都柏林\",\"zh_tw\":\"都柏林\"},{\"id\":\"91\",\"zone\":\"Pacific/Easter\",\"en\":\"Easter Island\",\"zh_cn\":\"复活节岛\",\"zh_hk\":\"復活節島\",\"zh_tw\":\"復活節島\"},{\"id\":\"92\",\"zone\":\"America/Edmonton\",\"en\":\"Edmonton\",\"zh_cn\":\"埃德蒙顿\",\"zh_hk\":\"愛民頓\",\"zh_tw\":\"艾德蒙頓\"},{\"id\":\"93\",\"zone\":\"America/El_Salvador\",\"en\":\"El Salvador\",\"zh_cn\":\"萨尔瓦多\",\"zh_hk\":\"聖薩爾瓦多\",\"zh_tw\":\"薩爾瓦多\"},{\"id\":\"94\",\"zone\":\"Pacific/Fiji\",\"en\":\"Fiji\",\"zh_cn\":\"斐济\",\"zh_hk\":\"斐濟\",\"zh_tw\":\"斐濟\"},{\"id\":\"95\",\"zone\":\"America/Fortaleza\",\"en\":\"Fortaleza\",\"zh_cn\":\"福塔雷萨\",\"zh_hk\":\"福塔雷薩\",\"zh_tw\":\"福塔雷薩\"},{\"id\":\"96\",\"zone\":\"Europe/Berlin\",\"en\":\"Frankfurt\",\"zh_cn\":\"法兰克福\",\"zh_hk\":\"法蘭克福\",\"zh_tw\":\"法蘭克福\"},{\"id\":\"97\",\"zone\":\"Africa/Freetown\",\"en\":\"Freetown\",\"zh_cn\":\"弗里敦\",\"zh_hk\":\"自由城\",\"zh_tw\":\"自由城\"},{\"id\":\"98\",\"zone\":\"Africa/Gaborone\",\"en\":\"Gaborone\",\"zh_cn\":\"哈博罗内\",\"zh_hk\":\"嘉柏隆里\",\"zh_tw\":\"嘉柏隆里\"},{\"id\":\"99\",\"zone\":\"Asia/Gaza\",\"en\":\"Gaza\",\"zh_cn\":\"加沙\",\"zh_hk\":\"加沙\",\"zh_tw\":\"加薩\"},{\"id\":\"100\",\"zone\":\"Europe/Gibraltar\",\"en\":\"Gibraltar\",\"zh_cn\":\"直布罗陀\",\"zh_hk\":\"直布羅陀\",\"zh_tw\":\"直布羅陀\"},{\"id\":\"101\",\"zone\":\"America/Grand_Turk\",\"en\":\"Grand Turk\",\"zh_cn\":\"大特克斯岛\",\"zh_hk\":\"大特克島\",\"zh_tw\":\"大特克島\"},{\"id\":\"102\",\"zone\":\"America/Grenada\",\"en\":\"Grenada\",\"zh_cn\":\"格林纳达\",\"zh_hk\":\"格瑞納達\",\"zh_tw\":\"格瑞那達\"},{\"id\":\"103\",\"zone\":\"Pacific/Guam\",\"en\":\"Guam\",\"zh_cn\":\"关岛\",\"zh_hk\":\"關島\",\"zh_tw\":\"關島\"},{\"id\":\"104\",\"zone\":\"Asia/Shanghai\",\"en\":\"Guangzhou\",\"zh_cn\":\"广州\",\"zh_hk\":\"廣州\",\"zh_tw\":\"廣州\"},{\"id\":\"105\",\"zone\":\"America/Guatemala\",\"en\":\"Guatemala\",\"zh_cn\":\"危地马拉\",\"zh_hk\":\"危地馬拉\",\"zh_tw\":\"瓜地馬拉\"},{\"id\":\"106\",\"zone\":\"Asia/Kolkata\",\"en\":\"Gurgaon\",\"zh_cn\":\"古尔冈\",\"zh_hk\":\"古爾岡\",\"zh_tw\":\"古爾岡\"},{\"id\":\"107\",\"zone\":\"America/Guyana\",\"en\":\"Guyana\",\"zh_cn\":\"圭亚那\",\"zh_hk\":\"圭亞那\",\"zh_tw\":\"蓋亞那\"},{\"id\":\"108\",\"zone\":\"Asia/Jerusalem\",\"en\":\"Haifa\",\"zh_cn\":\"海法\",\"zh_hk\":\"海法\",\"zh_tw\":\"海法\"},{\"id\":\"109\",\"zone\":\"America/Halifax\",\"en\":\"Halifax\",\"zh_cn\":\"哈利法克斯\",\"zh_hk\":\"哈利法克斯\",\"zh_tw\":\"哈利法克斯\"},{\"id\":\"110\",\"zone\":\"Europe/Berlin\",\"en\":\"Hamburg\",\"zh_cn\":\"汉堡\",\"zh_hk\":\"漢堡\",\"zh_tw\":\"漢堡\"},{\"id\":\"111\",\"zone\":\"Asia/Ho_Chi_Minh\",\"en\":\"Hanoi\",\"zh_cn\":\"河内\",\"zh_hk\":\"河內\",\"zh_tw\":\"河內\"},{\"id\":\"112\",\"zone\":\"Africa/Harare\",\"en\":\"Harare\",\"zh_cn\":\"哈拉雷\",\"zh_hk\":\"哈拉雷\",\"zh_tw\":\"哈拉雷\"},{\"id\":\"113\",\"zone\":\"America/Havana\",\"en\":\"Havana\",\"zh_cn\":\"哈瓦那\",\"zh_hk\":\"哈瓦那\",\"zh_tw\":\"哈瓦那\"},{\"id\":\"114\",\"zone\":\"Asia/Hebron\",\"en\":\"Hebron\",\"zh_cn\":\"希伯伦\",\"zh_hk\":\"希伯侖\",\"zh_tw\":\"希伯侖\"},{\"id\":\"115\",\"zone\":\"Europe/Helsinki\",\"en\":\"Helsinki\",\"zh_cn\":\"赫尔辛基\",\"zh_hk\":\"赫爾辛基\",\"zh_tw\":\"赫爾辛基\"},{\"id\":\"116\",\"zone\":\"Asia/Ho_Chi_Minh\",\"en\":\"Ho Chi Minh\",\"zh_cn\":\"胡志明市\",\"zh_hk\":\"胡志明市\",\"zh_tw\":\"胡志明市\"},{\"id\":\"117\",\"zone\":\"Asia/Hong_Kong\",\"en\":\"Hong Kong\",\"zh_cn\":\"香港\",\"zh_hk\":\"香港\",\"zh_tw\":\"香港\"},{\"id\":\"118\",\"zone\":\"Pacific/Honolulu\",\"en\":\"Honolulu\",\"zh_cn\":\"檀香山\",\"zh_hk\":\"檀香山\",\"zh_tw\":\"檀香山\"},{\"id\":\"119\",\"zone\":\"America/Chicago\",\"en\":\"Houston\",\"zh_cn\":\"休斯敦\",\"zh_hk\":\"侯士頓\",\"zh_tw\":\"休士頓\"},{\"id\":\"120\",\"zone\":\"Asia/Kolkata\",\"en\":\"Hyderabad\",\"zh_cn\":\"海得拉巴\",\"zh_hk\":\"海得拉巴\",\"zh_tw\":\"海德拉巴\"},{\"id\":\"121\",\"zone\":\"America/Indianapolis\",\"en\":\"Indianapolis\",\"zh_cn\":\"印第安纳波利斯\",\"zh_hk\":\"印第安納波利斯\",\"zh_tw\":\"印第安納波利斯\"},{\"id\":\"122\",\"zone\":\"Asia/Karachi\",\"en\":\"Islamabad\",\"zh_cn\":\"伊斯兰堡\",\"zh_hk\":\"伊斯坦堡\",\"zh_tw\":\"伊斯蘭瑪巴德\"},{\"id\":\"123\",\"zone\":\"Europe/Isle_of_Man\",\"en\":\"Isle of Man\",\"zh_cn\":\"马恩岛\",\"zh_hk\":\"曼島\",\"zh_tw\":\"曼島\"},{\"id\":\"124\",\"zone\":\"Europe/Istanbul\",\"en\":\"Istanbul\",\"zh_cn\":\"伊斯坦布尔\",\"zh_hk\":\"伊斯坦堡\",\"zh_tw\":\"伊斯坦堡\"},{\"id\":\"125\",\"zone\":\"America/New_York\",\"en\":\"Jacksonville\",\"zh_cn\":\"杰克逊维尔\",\"zh_hk\":\"傑克遜維爾\",\"zh_tw\":\"傑克遜維爾\"},{\"id\":\"126\",\"zone\":\"Asia/Jakarta\",\"en\":\"Jakarta\",\"zh_cn\":\"雅加达\",\"zh_hk\":\"雅加達\",\"zh_tw\":\"雅加達\"},{\"id\":\"127\",\"zone\":\"Asia/Jerusalem\",\"en\":\"Jerusalem\",\"zh_cn\":\"耶路撒冷\",\"zh_hk\":\"耶路撒冷\",\"zh_tw\":\"耶路撒冷\"},{\"id\":\"128\",\"zone\":\"Africa/Johannesburg\",\"en\":\"Johannesburg\",\"zh_cn\":\"约翰内斯堡\",\"zh_hk\":\"約翰內斯堡\",\"zh_tw\":\"約翰尼斯堡\"},{\"id\":\"129\",\"zone\":\"Asia/Kabul\",\"en\":\"Kabul\",\"zh_cn\":\"喀布尔\",\"zh_hk\":\"喀布爾\",\"zh_tw\":\"喀布爾\"},{\"id\":\"130\",\"zone\":\"Africa/Kampala\",\"en\":\"Kampala\",\"zh_cn\":\"坎帕拉\",\"zh_hk\":\"坎帕拉\",\"zh_tw\":\"坎帕拉\"},{\"id\":\"131\",\"zone\":\"Asia/Karachi\",\"en\":\"Karachi\",\"zh_cn\":\"卡拉奇\",\"zh_hk\":\"卡拉奇\",\"zh_tw\":\"喀拉蚩\"},{\"id\":\"132\",\"zone\":\"Asia/Kathmandu\",\"en\":\"Kathmandu\",\"zh_cn\":\"加德满都\",\"zh_hk\":\"加德滿都\",\"zh_tw\":\"加德滿都\"},{\"id\":\"133\",\"zone\":\"Africa/Khartoum\",\"en\":\"Khartoum\",\"zh_cn\":\"哈土穆\",\"zh_hk\":\"哈土穆\",\"zh_tw\":\"哈土穆\"},{\"id\":\"134\",\"zone\":\"Africa/Kigali\",\"en\":\"Kigali\",\"zh_cn\":\"基加利\",\"zh_hk\":\"基加利\",\"zh_tw\":\"吉佳利\"},{\"id\":\"135\",\"zone\":\"America/Jamaica\",\"en\":\"Kingston\",\"zh_cn\":\"金斯敦\",\"zh_hk\":\"京斯敦\",\"zh_tw\":\"京斯敦\"},{\"id\":\"136\",\"zone\":\"Africa/Kinshasa\",\"en\":\"Kinshasa\",\"zh_cn\":\"金沙萨\",\"zh_hk\":\"金沙萨\",\"zh_tw\":\"金夏沙\"},{\"id\":\"137\",\"zone\":\"Pacific/Kiritimati\",\"en\":\"Kiritimati\",\"zh_cn\":\"圣诞岛\",\"zh_hk\":\"聖誕島\",\"zh_tw\":\"聖誕島\"},{\"id\":\"138\",\"zone\":\"America/Los_Angeles\",\"en\":\"Kirkland\",\"zh_cn\":\"柯克兰\",\"zh_hk\":\"柯克蘭\",\"zh_tw\":\"柯克蘭\"},{\"id\":\"139\",\"zone\":\"America/Chicago\",\"en\":\"Knox City\",\"zh_cn\":\"诺克斯\",\"zh_hk\":\"諾克斯\",\"zh_tw\":\"諾克斯\"},{\"id\":\"140\",\"zone\":\"America/New_York\",\"en\":\"Knoxville\",\"zh_cn\":\"诺克斯维尔\",\"zh_hk\":\"諾克斯維爾\",\"zh_tw\":\"諾克斯維爾\"},{\"id\":\"141\",\"zone\":\"Europe/Warsaw\",\"en\":\"Kraków\",\"zh_cn\":\"克拉科夫\",\"zh_hk\":\"克拉科夫\",\"zh_tw\":\"克拉科夫\"},{\"id\":\"142\",\"zone\":\"Asia/Kuala_Lumpur\",\"en\":\"Kuala Lumpur\",\"zh_cn\":\"吉隆坡\",\"zh_hk\":\"吉隆坡\",\"zh_tw\":\"吉隆坡\"},{\"id\":\"143\",\"zone\":\"Asia/Kuwait\",\"en\":\"Kuwait\",\"zh_cn\":\"科威特\",\"zh_hk\":\"科威特\",\"zh_tw\":\"科威特\"},{\"id\":\"144\",\"zone\":\"Europe/Kiev\",\"en\":\"Kyiv\",\"zh_cn\":\"基辅\",\"zh_hk\":\"基輔\",\"zh_tw\":\"基輔\"},{\"id\":\"145\",\"zone\":\"America/La_Paz\",\"en\":\"La Paz\",\"zh_cn\":\"拉巴斯\",\"zh_hk\":\"拉巴斯\",\"zh_tw\":\"拉巴斯\"},{\"id\":\"146\",\"zone\":\"Africa/Lagos\",\"en\":\"Lagos\",\"zh_cn\":\"拉各斯\",\"zh_hk\":\"拉各斯\",\"zh_tw\":\"拉哥斯\"},{\"id\":\"147\",\"zone\":\"Asia/Karachi\",\"en\":\"Lahore\",\"zh_cn\":\"拉合尔\",\"zh_hk\":\"拉合爾\",\"zh_tw\":\"拉合爾\"},{\"id\":\"148\",\"zone\":\"America/Los_Angeles\",\"en\":\"Las Vegas\",\"zh_cn\":\"拉斯维加斯\",\"zh_hk\":\"拉斯維加斯\",\"zh_tw\":\"拉斯維加斯\"},{\"id\":\"149\",\"zone\":\"America/Lima\",\"en\":\"Lima\",\"zh_cn\":\"利马\",\"zh_hk\":\"利馬\",\"zh_tw\":\"利馬\"},{\"id\":\"150\",\"zone\":\"Europe/Lisbon\",\"en\":\"Lisbon\",\"zh_cn\":\"里斯本\",\"zh_hk\":\"里斯本\",\"zh_tw\":\"里斯本\"},{\"id\":\"151\",\"zone\":\"Europe/London\",\"en\":\"London\",\"zh_cn\":\"伦敦\",\"zh_hk\":\"倫敦\",\"zh_tw\":\"倫敦\"},{\"id\":\"152\",\"zone\":\"Arctic/Longyearbyen\",\"en\":\"Longyearbyen\",\"zh_cn\":\"朗伊尔城\",\"zh_hk\":\"朗伊尔城\",\"zh_tw\":\"龍宜爾比恩\"},{\"id\":\"153\",\"zone\":\"America/Los_Angeles\",\"en\":\"Los Angeles\",\"zh_cn\":\"洛杉矶\",\"zh_hk\":\"洛杉磯\",\"zh_tw\":\"洛杉磯\"},{\"id\":\"154\",\"zone\":\"America/Louisville\",\"en\":\"Louisville\",\"zh_cn\":\"路易斯维尔\",\"zh_hk\":\"路易維爾\",\"zh_tw\":\"路易維爾\"},{\"id\":\"155\",\"zone\":\"Europe/Luxembourg\",\"en\":\"Luxembourg\",\"zh_cn\":\"卢森堡\",\"zh_hk\":\"盧森堡\",\"zh_tw\":\"盧森堡\"},{\"id\":\"156\",\"zone\":\"Asia/Macau\",\"en\":\"Macau\",\"zh_cn\":\"澳门\",\"zh_hk\":\"澳門\",\"zh_tw\":\"澳門\"},{\"id\":\"157\",\"zone\":\"America/Chicago\",\"en\":\"Madison\",\"zh_cn\":\"麦迪逊\",\"zh_hk\":\"麥迪遜\",\"zh_tw\":\"麥迪遜\"},{\"id\":\"158\",\"zone\":\"Europe/Madrid\",\"en\":\"Madrid\",\"zh_cn\":\"马德里\",\"zh_hk\":\"馬德里\",\"zh_tw\":\"馬德里\"},{\"id\":\"159\",\"zone\":\"Indian/Maldives\",\"en\":\"Maldives\",\"zh_cn\":\"马尔代夫\",\"zh_hk\":\"馬爾地夫\",\"zh_tw\":\"馬爾地夫\"},{\"id\":\"160\",\"zone\":\"Europe/Malta\",\"en\":\"Malta\",\"zh_cn\":\"马耳他\",\"zh_hk\":\"瓦勒他\",\"zh_tw\":\"馬爾他\"},{\"id\":\"161\",\"zone\":\"America/Managua\",\"en\":\"Managua\",\"zh_cn\":\"马那瓜\",\"zh_hk\":\"馬那瓜\",\"zh_tw\":\"馬拿瓜\"},{\"id\":\"162\",\"zone\":\"Europe/London\",\"en\":\"Manchester\",\"zh_cn\":\"曼彻斯特\",\"zh_hk\":\"曼徹斯特\",\"zh_tw\":\"曼徹斯特\"},{\"id\":\"163\",\"zone\":\"Asia/Manila\",\"en\":\"Manila\",\"zh_cn\":\"马尼拉\",\"zh_hk\":\"馬尼拉\",\"zh_tw\":\"馬尼拉\"},{\"id\":\"164\",\"zone\":\"America/Martinique\",\"en\":\"Martinique\",\"zh_cn\":\"马提尼克\",\"zh_hk\":\"馬提尼克\",\"zh_tw\":\"馬丁尼克\"},{\"id\":\"165\",\"zone\":\"Africa/Maseru\",\"en\":\"Maseru\",\"zh_cn\":\"马塞卢\",\"zh_hk\":\"馬塞盧\",\"zh_tw\":\"馬塞盧\"},{\"id\":\"166\",\"zone\":\"Indian/Mauritius\",\"en\":\"Mauritius\",\"zh_cn\":\"毛里求斯\",\"zh_hk\":\"毛里裘斯\",\"zh_tw\":\"模里西斯\"},{\"id\":\"167\",\"zone\":\"Australia/Melbourne\",\"en\":\"Melbourne\",\"zh_cn\":\"墨尔本\",\"zh_hk\":\"墨爾本\",\"zh_tw\":\"墨爾本\"},{\"id\":\"168\",\"zone\":\"America/Chicago\",\"en\":\"Memphis\",\"zh_cn\":\"孟菲斯\",\"zh_hk\":\"孟菲斯\",\"zh_tw\":\"孟菲斯\"},{\"id\":\"169\",\"zone\":\"America/Mendoza\",\"en\":\"Mendoza\",\"zh_cn\":\"门多萨\",\"zh_hk\":\"門多薩\",\"zh_tw\":\"門多薩\"},{\"id\":\"170\",\"zone\":\"America/Metlakatla\",\"en\":\"Metlakatla\",\"zh_cn\":\"梅特拉卡特拉\",\"zh_hk\":\"梅特拉卡特拉\",\"zh_tw\":\"梅特拉卡特拉\"},{\"id\":\"171\",\"zone\":\"America/Mexico_City\",\"en\":\"Mexico City\",\"zh_cn\":\"墨西哥城\",\"zh_hk\":\"墨西哥城\",\"zh_tw\":\"墨西哥城\"},{\"id\":\"172\",\"zone\":\"America/New_York\",\"en\":\"Miami\",\"zh_cn\":\"迈阿密\",\"zh_hk\":\"邁阿密\",\"zh_tw\":\"邁阿密\"},{\"id\":\"173\",\"zone\":\"Europe/Rome\",\"en\":\"Milan\",\"zh_cn\":\"米兰\",\"zh_hk\":\"米蘭\",\"zh_tw\":\"米蘭\"},{\"id\":\"174\",\"zone\":\"America/Chicago\",\"en\":\"Milwaukee\",\"zh_cn\":\"密尔沃基\",\"zh_hk\":\"密爾沃基\",\"zh_tw\":\"密爾瓦基\"},{\"id\":\"175\",\"zone\":\"America/Chicago\",\"en\":\"Minneapolis\",\"zh_cn\":\"明尼阿波里斯\",\"zh_hk\":\"明尼阿波利斯\",\"zh_tw\":\"明尼亞波利斯\"},{\"id\":\"176\",\"zone\":\"Europe/Minsk\",\"en\":\"Minsk\",\"zh_cn\":\"明斯克\",\"zh_hk\":\"明斯克\",\"zh_tw\":\"明斯克\"},{\"id\":\"177\",\"zone\":\"Africa/Mogadishu\",\"en\":\"Mogadishu\",\"zh_cn\":\"摩加迪沙\",\"zh_hk\":\"摩加迪沙\",\"zh_tw\":\"摩加迪休\"},{\"id\":\"178\",\"zone\":\"Europe/Monaco\",\"en\":\"Monaco\",\"zh_cn\":\"摩纳哥\",\"zh_hk\":\"摩納哥\",\"zh_tw\":\"摩納哥\"},{\"id\":\"179\",\"zone\":\"Africa/Monrovia\",\"en\":\"Monrovia\",\"zh_cn\":\"蒙罗维亚\",\"zh_hk\":\"蒙羅維亞\",\"zh_tw\":\"蒙羅維亞\"},{\"id\":\"180\",\"zone\":\"America/Monterrey\",\"en\":\"Monterrey\",\"zh_cn\":\"蒙特雷\",\"zh_hk\":\"蒙特雷\",\"zh_tw\":\"蒙特雷\"},{\"id\":\"181\",\"zone\":\"America/Montevideo\",\"en\":\"Montevideo\",\"zh_cn\":\"蒙得维的亚\",\"zh_hk\":\"蒙特維的亞\",\"zh_tw\":\"蒙特維多\"},{\"id\":\"182\",\"zone\":\"America/Montreal\",\"en\":\"Montreal\",\"zh_cn\":\"蒙特利尔\",\"zh_hk\":\"滿地可\",\"zh_tw\":\"蒙特婁\"},{\"id\":\"183\",\"zone\":\"Europe/Moscow\",\"en\":\"Moscow\",\"zh_cn\":\"莫斯科\",\"zh_hk\":\"莫斯科\",\"zh_tw\":\"莫斯科\"},{\"id\":\"184\",\"zone\":\"America/Los_Angeles\",\"en\":\"Mountain View\",\"zh_cn\":\"山景城\",\"zh_hk\":\"山景城\",\"zh_tw\":\"山景城\"},{\"id\":\"185\",\"zone\":\"Asia/Kolkata\",\"en\":\"Mumbai\",\"zh_cn\":\"孟买\",\"zh_hk\":\"孟買\",\"zh_tw\":\"孟買\"},{\"id\":\"186\",\"zone\":\"Europe/Berlin\",\"en\":\"Munich\",\"zh_cn\":\"慕尼黑\",\"zh_hk\":\"慕尼黑\",\"zh_tw\":\"慕尼黑\"},{\"id\":\"187\",\"zone\":\"Asia/Muscat\",\"en\":\"Muscat\",\"zh_cn\":\"马斯喀特\",\"zh_hk\":\"馬斯喀特\",\"zh_tw\":\"馬斯喀特\"},{\"id\":\"188\",\"zone\":\"Africa/Nairobi\",\"en\":\"Nairobi\",\"zh_cn\":\"奈洛比\",\"zh_hk\":\"奈洛比\",\"zh_tw\":\"奈洛比\"},{\"id\":\"189\",\"zone\":\"America/Chicago\",\"en\":\"Nashville\",\"zh_cn\":\"纳什维尔\",\"zh_hk\":\"納什維爾\",\"zh_tw\":\"納許維爾\"},{\"id\":\"190\",\"zone\":\"America/Nassau\",\"en\":\"Nassau\",\"zh_cn\":\"拿骚\",\"zh_hk\":\"拿騷\",\"zh_tw\":\"拿索\"},{\"id\":\"191\",\"zone\":\"America/Chicago\",\"en\":\"New Orleans\",\"zh_cn\":\"新奥尔良\",\"zh_hk\":\"紐奧良\",\"zh_tw\":\"紐奧良\"},{\"id\":\"192\",\"zone\":\"America/North_Dakota/New_Salem\",\"en\":\"New Salem\",\"zh_cn\":\"新塞勒姆\",\"zh_hk\":\"新塞勒姆\",\"zh_tw\":\"新塞勒姆\"},{\"id\":\"193\",\"zone\":\"Australia/Sydney\",\"en\":\"New South Wales\",\"zh_cn\":\"新南威尔士\",\"zh_hk\":\"新南威爾斯州\",\"zh_tw\":\"新南威爾斯州\"},{\"id\":\"194\",\"zone\":\"America/New_York\",\"en\":\"New York\",\"zh_cn\":\"纽约\",\"zh_hk\":\"紐約\",\"zh_tw\":\"紐約\"},{\"id\":\"195\",\"zone\":\"Canada/Newfoundland\",\"en\":\"Newfoundland\",\"zh_cn\":\"纽芬兰\",\"zh_hk\":\"紐芬蘭\",\"zh_tw\":\"紐芬蘭\"},{\"id\":\"196\",\"zone\":\"Pacific/Noumea\",\"en\":\"Noumea\",\"zh_cn\":\"努美阿\",\"zh_hk\":\"努美阿\",\"zh_tw\":\"努美阿\"},{\"id\":\"197\",\"zone\":\"America/Chicago\",\"en\":\"Oklahoma City\",\"zh_cn\":\"俄克拉何马城\",\"zh_hk\":\"奧克拉荷馬市\",\"zh_tw\":\"奧克拉荷馬市\"},{\"id\":\"198\",\"zone\":\"Asia/Tokyo\",\"en\":\"Osaka\",\"zh_cn\":\"大阪\",\"zh_hk\":\"大阪\",\"zh_tw\":\"大阪\"},{\"id\":\"199\",\"zone\":\"Europe/Oslo\",\"en\":\"Oslo\",\"zh_cn\":\"奥斯陆\",\"zh_hk\":\"奧斯陸\",\"zh_tw\":\"奧斯陸\"},{\"id\":\"200\",\"zone\":\"America/Toronto\",\"en\":\"Ottawa\",\"zh_cn\":\"渥太华\",\"zh_hk\":\"渥太華\",\"zh_tw\":\"渥太華\"},{\"id\":\"201\",\"zone\":\"Europe/Helsinki\",\"en\":\"Oulu\",\"zh_cn\":\"奥卢\",\"zh_hk\":\"奧盧\",\"zh_tw\":\"奧盧\"},{\"id\":\"202\",\"zone\":\"America/Panama\",\"en\":\"Panama\",\"zh_cn\":\"巴拿马\",\"zh_hk\":\"巴拿馬\",\"zh_tw\":\"巴拿馬\"},{\"id\":\"203\",\"zone\":\"America/Paramaribo\",\"en\":\"Paramaribo\",\"zh_cn\":\"帕拉马里博\",\"zh_hk\":\"巴拉馬利波\",\"zh_tw\":\"巴拉馬利波\"},{\"id\":\"204\",\"zone\":\"Europe/Paris\",\"en\":\"Paris\",\"zh_cn\":\"巴黎\",\"zh_hk\":\"巴黎\",\"zh_tw\":\"巴黎\"},{\"id\":\"205\",\"zone\":\"Australia/Perth\",\"en\":\"Perth\",\"zh_cn\":\"珀斯\",\"zh_hk\":\"珀斯\",\"zh_tw\":\"伯斯\"},{\"id\":\"206\",\"zone\":\"America/Indiana/Petersburg\",\"en\":\"Petersburg\",\"zh_cn\":\"彼得斯堡\",\"zh_hk\":\"彼得斯堡\",\"zh_tw\":\"彼得斯堡\"},{\"id\":\"207\",\"zone\":\"America/New_York\",\"en\":\"Philadelphia\",\"zh_cn\":\"费城\",\"zh_hk\":\"費城\",\"zh_tw\":\"費城\"},{\"id\":\"208\",\"zone\":\"Asia/Phnom_Penh\",\"en\":\"Phnom Penh\",\"zh_cn\":\"金边\",\"zh_hk\":\"金邊\",\"zh_tw\":\"金邊\"},{\"id\":\"209\",\"zone\":\"America/Phoenix\",\"en\":\"Phoenix\",\"zh_cn\":\"菲尼克斯\",\"zh_hk\":\"鳳凰城\",\"zh_tw\":\"鳳凰城\"},{\"id\":\"210\",\"zone\":\"America/New_York\",\"en\":\"Pittsburgh\",\"zh_cn\":\"匹兹堡\",\"zh_hk\":\"匹茲堡\",\"zh_tw\":\"匹茲堡\"},{\"id\":\"211\",\"zone\":\"America/Port_of_Spain\",\"en\":\"Port of Spain\",\"zh_cn\":\"西班牙港\",\"zh_hk\":\"西班牙港\",\"zh_tw\":\"西班牙港\"},{\"id\":\"212\",\"zone\":\"America/Port-au-Prince\",\"en\":\"Port-au-Prince\",\"zh_cn\":\"太子港\",\"zh_hk\":\"太子港\",\"zh_tw\":\"太子港\"},{\"id\":\"213\",\"zone\":\"America/Los_Angeles\",\"en\":\"Portland\",\"zh_cn\":\"波特兰\",\"zh_hk\":\"波特蘭\",\"zh_tw\":\"波特蘭\"},{\"id\":\"214\",\"zone\":\"Europe/Prague\",\"en\":\"Prague\",\"zh_cn\":\"布拉格\",\"zh_hk\":\"布拉格\",\"zh_tw\":\"布拉格\"},{\"id\":\"215\",\"zone\":\"America/Puerto_Rico\",\"en\":\"Puerto Rico\",\"zh_cn\":\"波多黎各\",\"zh_hk\":\"波多黎各\",\"zh_tw\":\"波多黎各\"},{\"id\":\"216\",\"zone\":\"Asia/Seoul\",\"en\":\"Pyongyang\",\"zh_cn\":\"平壤\",\"zh_hk\":\"平壤\",\"zh_tw\":\"平壤\"},{\"id\":\"217\",\"zone\":\"Asia/Qatar\",\"en\":\"Qatar\",\"zh_cn\":\"卡塔尔\",\"zh_hk\":\"卡塔爾\",\"zh_tw\":\"卡達\"},{\"id\":\"218\",\"zone\":\"Australia/Queensland\",\"en\":\"Queensland\",\"zh_cn\":\"昆士兰\",\"zh_hk\":\"昆士蘭\",\"zh_tw\":\"昆士蘭\"},{\"id\":\"219\",\"zone\":\"America/Guayaquil\",\"en\":\"Quito\",\"zh_cn\":\"基多\",\"zh_hk\":\"基多\",\"zh_tw\":\"基多\"},{\"id\":\"220\",\"zone\":\"Asia/Rangoon\",\"en\":\"Rangoon\",\"zh_cn\":\"仰光\",\"zh_hk\":\"仰光\",\"zh_tw\":\"仰光\"},{\"id\":\"221\",\"zone\":\"America/Los_Angeles\",\"en\":\"Reno\",\"zh_cn\":\"里诺\",\"zh_hk\":\"雷諾\",\"zh_tw\":\"雷諾\"},{\"id\":\"222\",\"zone\":\"America/New_York\",\"en\":\"Reston\",\"zh_cn\":\"雷斯顿\",\"zh_hk\":\"雷斯頓\",\"zh_tw\":\"雷斯頓\"},{\"id\":\"223\",\"zone\":\"Atlantic/Reykjavik\",\"en\":\"Reykjavik\",\"zh_cn\":\"雷克雅末克\",\"zh_hk\":\"雷克雅維克\",\"zh_tw\":\"雷克雅維克\"},{\"id\":\"224\",\"zone\":\"Europe/Riga\",\"en\":\"Riga\",\"zh_cn\":\"里加\",\"zh_hk\":\"里加\",\"zh_tw\":\"紐芬蘭島\"},{\"id\":\"225\",\"zone\":\"America/Sao_Paulo\",\"en\":\"Rio de Janeiro\",\"zh_cn\":\"里约热内卢\",\"zh_hk\":\"里約熱內盧\",\"zh_tw\":\"里約熱內盧\"},{\"id\":\"226\",\"zone\":\"Asia/Riyadh\",\"en\":\"Riyadh\",\"zh_cn\":\"利雅得\",\"zh_hk\":\"利雅德\",\"zh_tw\":\"利雅德\"},{\"id\":\"227\",\"zone\":\"Europe/Rome\",\"en\":\"Rome\",\"zh_cn\":\"罗马\",\"zh_hk\":\"羅馬\",\"zh_tw\":\"羅馬\"},{\"id\":\"228\",\"zone\":\"America/Los_Angeles\",\"en\":\"Sacramento\",\"zh_cn\":\"萨克拉门托\",\"zh_hk\":\"薩克拉門托\",\"zh_tw\":\"沙加緬度\"},{\"id\":\"229\",\"zone\":\"America/Denver\",\"en\":\"Salt Lake City\",\"zh_cn\":\"盐湖城\",\"zh_hk\":\"鹽湖城\",\"zh_tw\":\"鹽湖城\"},{\"id\":\"230\",\"zone\":\"Pacific/Samoa\",\"en\":\"Samoa\",\"zh_cn\":\"萨摩亚\",\"zh_hk\":\"薩摩亞\",\"zh_tw\":\"薩摩亞\"},{\"id\":\"231\",\"zone\":\"America/Chicago\",\"en\":\"San Antonio\",\"zh_cn\":\"圣安东尼奥\",\"zh_hk\":\"聖安東尼奧\",\"zh_tw\":\"聖安東尼奧\"},{\"id\":\"232\",\"zone\":\"America/Los_Angeles\",\"en\":\"San Diego\",\"zh_cn\":\"圣迭戈\",\"zh_hk\":\"聖達戈\",\"zh_tw\":\"聖達戈\"},{\"id\":\"233\",\"zone\":\"America/Los_Angeles\",\"en\":\"San Francisco\",\"zh_cn\":\"旧金山\",\"zh_hk\":\"舊金山\",\"zh_tw\":\"舊金山\"},{\"id\":\"234\",\"zone\":\"America/Los_Angeles\",\"en\":\"San Jose\",\"zh_cn\":\"圣何塞\",\"zh_hk\":\"聖荷西\",\"zh_tw\":\"聖荷西\"},\n" +
                "{\"id\":\"236\",\"zone\":\"Europe/San_Marino\",\"en\":\"San Marino\",\"zh_cn\":\"圣马力诺\",\"zh_hk\":\"聖馬力諾\",\"zh_tw\":\"聖馬力諾\"},{\"id\":\"237\",\"zone\":\"Asia/Aden\",\"en\":\"Sana'a\",\"zh_cn\":\"萨纳\",\"zh_hk\":\"沙那\",\"zh_tw\":\"沙那\"},{\"id\":\"238\",\"zone\":\"America/Santiago\",\"en\":\"Santiago\",\"zh_cn\":\"圣地亚哥\",\"zh_hk\":\"聖地亞哥\",\"zh_tw\":\"聖地牙哥\"},{\"id\":\"239\",\"zone\":\"America/Santo_Domingo\",\"en\":\"Santo Domingo\",\"zh_cn\":\"圣多明戈\",\"zh_hk\":\"聖多明戈\",\"zh_tw\":\"聖多明哥\"},{\"id\":\"240\",\"zone\":\"America/Sao_Paulo\",\"en\":\"Sao Paulo\",\"zh_cn\":\"圣保罗\",\"zh_hk\":\"聖保羅\",\"zh_tw\":\"聖保羅\"},{\"id\":\"241\",\"zone\":\"Africa/Sao_Tome\",\"en\":\"Sao Tomé\",\"zh_cn\":\"圣多美\",\"zh_hk\":\"聖多美\",\"zh_tw\":\"聖多美\"},{\"id\":\"242\",\"zone\":\"Europe/Sarajevo\",\"en\":\"Sarajevo\",\"zh_cn\":\"萨拉热窝\",\"zh_hk\":\"塞拉熱窩\",\"zh_tw\":\"塞拉耶佛\"},{\"id\":\"243\",\"zone\":\"Canada/Saskatchewan\",\"en\":\"Saskatchewan\",\"zh_cn\":\"萨斯喀彻温\",\"zh_hk\":\"薩斯卡通\",\"zh_tw\":\"薩斯喀徹溫\"},{\"id\":\"244\",\"zone\":\"America/Los_Angeles\",\"en\":\"Seattle\",\"zh_cn\":\"西雅图\",\"zh_hk\":\"西雅圖\",\"zh_tw\":\"西雅圖\"},{\"id\":\"245\",\"zone\":\"Asia/Seoul\",\"en\":\"Seoul\",\"zh_cn\":\"首尔\",\"zh_hk\":\"首爾\",\"zh_tw\":\"首爾\"},{\"id\":\"246\",\"zone\":\"Asia/Shanghai\",\"en\":\"Shanghai\",\"zh_cn\":\"上海\",\"zh_hk\":\"上海\",\"zh_tw\":\"上海\"},{\"id\":\"247\",\"zone\":\"Europe/Simferopol\",\"en\":\"Simferopol\",\"zh_cn\":\"辛菲罗波尔\",\"zh_hk\":\"辛菲羅波爾\",\"zh_tw\":\"辛菲羅波爾\"},{\"id\":\"248\",\"zone\":\"Asia/Singapore\",\"en\":\"Singapore\",\"zh_cn\":\"新加坡\",\"zh_hk\":\"新加坡\",\"zh_tw\":\"新加坡\"},{\"id\":\"249\",\"zone\":\"Europe/Skopje\",\"en\":\"Skopje\",\"zh_cn\":\"斯科普里\",\"zh_hk\":\"斯科普里\",\"zh_tw\":\"史高比耶\"},{\"id\":\"250\",\"zone\":\"Europe/Sofia\",\"en\":\"Sofia\",\"zh_cn\":\"索非亚\",\"zh_hk\":\"索菲亞\",\"zh_tw\":\"索菲亞\"},{\"id\":\"251\",\"zone\":\"Antarctica/South_Pole\",\"en\":\"South Pole\",\"zh_cn\":\"南极\",\"zh_hk\":\"南極\",\"zh_tw\":\"南極\"},{\"id\":\"252\",\"zone\":\"America/St_Johns\",\"en\":\"St. Johns\",\"zh_cn\":\"圣约翰\",\"zh_hk\":\"聖約翰\",\"zh_tw\":\"聖約翰\"},{\"id\":\"253\",\"zone\":\"America/St_Kitts\",\"en\":\"St. Kitts\",\"zh_cn\":\"圣基茨\",\"zh_hk\":\"聖基茨\",\"zh_tw\":\"聖基茨島\"},{\"id\":\"254\",\"zone\":\"America/Chicago\",\"en\":\"St. Louis\",\"zh_cn\":\"圣路易斯\",\"zh_hk\":\"聖路易斯\",\"zh_tw\":\"聖路易斯\"},{\"id\":\"255\",\"zone\":\"America/St_Lucia\",\"en\":\"St. Lucia\",\"zh_cn\":\"圣卢西亚\",\"zh_hk\":\"聖盧西亞\",\"zh_tw\":\"聖露西亞\"},{\"id\":\"256\",\"zone\":\"Europe/Moscow\",\"en\":\"St. Petersburg\",\"zh_cn\":\"圣彼得堡\",\"zh_hk\":\"聖彼得堡\",\"zh_tw\":\"聖彼得堡\"},{\"id\":\"257\",\"zone\":\"America/Toronto\",\"en\":\"St. Thomas\",\"zh_cn\":\"圣托马斯\",\"zh_hk\":\"聖托馬斯\",\"zh_tw\":\"聖托馬斯\"},{\"id\":\"258\",\"zone\":\"Europe/Stockholm\",\"en\":\"Stockholm\",\"zh_cn\":\"斯德哥尔摩\",\"zh_hk\":\"斯德哥爾摩\",\"zh_tw\":\"斯德哥爾\"},{\"id\":\"259\",\"zone\":\"Australia/Sydney\",\"en\":\"Sydney\",\"zh_cn\":\"悉尼\",\"zh_hk\":\"悉尼\",\"zh_tw\":\"雪梨\"},{\"id\":\"260\",\"zone\":\"Pacific/Tahiti\",\"en\":\"Tahiti\",\"zh_cn\":\"塔希提岛\",\"zh_hk\":\"大溪地\",\"zh_tw\":\"大溪地\"},{\"id\":\"261\",\"zone\":\"Asia/Taipei\",\"en\":\"Taipei\",\"zh_cn\":\"台北\",\"zh_hk\":\"臺北\",\"zh_tw\":\"台北\"},{\"id\":\"262\",\"zone\":\"Europe/Tallinn\",\"en\":\"Tallinn\",\"zh_cn\":\"塔林\",\"zh_hk\":\"塔林\",\"zh_tw\":\"塔林\"},{\"id\":\"263\",\"zone\":\"America/New_York\",\"en\":\"Tampa\",\"zh_cn\":\"坦帕\",\"zh_hk\":\"坦帕\",\"zh_tw\":\"坦帕\"},{\"id\":\"264\",\"zone\":\"America/Tegucigalpa\",\"en\":\"Tegucigalpa\",\"zh_cn\":\"特古西加尔巴\",\"zh_hk\":\"德古斯加爾巴\",\"zh_tw\":\"德古斯加巴\"},{\"id\":\"265\",\"zone\":\"Asia/Tehran\",\"en\":\"Tehran\",\"zh_cn\":\"德黑兰\",\"zh_hk\":\"德黑蘭\",\"zh_tw\":\"德黑蘭\"},{\"id\":\"266\",\"zone\":\"Asia/Jerusalem\",\"en\":\"Tel Aviv-Yafo\",\"zh_cn\":\"特拉维夫\",\"zh_hk\":\"特拉維夫\",\"zh_tw\":\"特拉維夫\"},{\"id\":\"267\",\"zone\":\"America/Indiana/Tell_City\",\"en\":\"Tell City\",\"zh_cn\":\"特尔城\",\"zh_hk\":\"特爾城\",\"zh_tw\":\"特爾城\"},{\"id\":\"268\",\"zone\":\"America/Tijuana\",\"en\":\"Tijuana\",\"zh_cn\":\"蒂华纳\",\"zh_hk\":\"蒂華納\",\"zh_tw\":\"提華納\"},{\"id\":\"269\",\"zone\":\"Africa/Timbuktu\",\"en\":\"Timbuktu\",\"zh_cn\":\"廷巴克图\",\"zh_hk\":\"廷巴克圖\",\"zh_tw\":\"廷巴克圖\"},{\"id\":\"270\",\"zone\":\"Asia/Tokyo\",\"en\":\"Tokyo\",\"zh_cn\":\"东京\",\"zh_hk\":\"東京\",\"zh_tw\":\"東京\"},{\"id\":\"271\",\"zone\":\"America/Toronto\",\"en\":\"Toronto\",\"zh_cn\":\"多伦多\",\"zh_hk\":\"多倫多\",\"zh_tw\":\"多倫多\"},{\"id\":\"272\",\"zone\":\"Africa/Tripoli\",\"en\":\"Tripoli\",\"zh_cn\":\"的黎波里\",\"zh_hk\":\"的黎波里\",\"zh_tw\":\"的黎波里\"},{\"id\":\"273\",\"zone\":\"America/Phoenix\",\"en\":\"Tucson\",\"zh_cn\":\"图森\",\"zh_hk\":\"圖森\",\"zh_tw\":\"土桑\"},{\"id\":\"274\",\"zone\":\"Africa/Tunis\",\"en\":\"Tunis\",\"zh_cn\":\"突尼斯\",\"zh_hk\":\"突尼西亞\",\"zh_tw\":\"突尼西亞\"},{\"id\":\"275\",\"zone\":\"Asia/Ulaanbaatar\",\"en\":\"Ulaanbaatar\",\"zh_cn\":\"乌兰巴托\",\"zh_hk\":\"烏蘭巴托\",\"zh_tw\":\"烏蘭巴托\"},{\"id\":\"276\",\"zone\":\"America/Vancouver\",\"en\":\"Vancouver\",\"zh_cn\":\"温哥华\",\"zh_hk\":\"溫哥華\",\"zh_tw\":\"溫哥華\"},{\"id\":\"277\",\"zone\":\"Europe/Vatican\",\"en\":\"Vatican City\",\"zh_cn\":\"梵蒂冈\",\"zh_hk\":\"梵蒂岡\",\"zh_tw\":\"梵蒂岡\"},{\"id\":\"278\",\"zone\":\"America/Indiana/Vevay\",\"en\":\"Vevay\",\"zh_cn\":\"韦韦\",\"zh_hk\":\"韋韋\",\"zh_tw\":\"韋韋\"},{\"id\":\"279\",\"zone\":\"Australia/Victoria\",\"en\":\"Victoria\",\"zh_cn\":\"维多利亚\",\"zh_hk\":\"維多利亞\",\"zh_tw\":\"維多利亞\"},{\"id\":\"280\",\"zone\":\"Europe/Vienna\",\"en\":\"Vienna\",\"zh_cn\":\"维也纳\",\"zh_hk\":\"維也納\",\"zh_tw\":\"維也納\"},{\"id\":\"281\",\"zone\":\"Europe/Vilnius\",\"en\":\"Vilnius\",\"zh_cn\":\"维尔纽斯\",\"zh_hk\":\"維爾紐斯\",\"zh_tw\":\"維爾紐斯\"},{\"id\":\"282\",\"zone\":\"America/Indiana/Vincennes\",\"en\":\"Vincennes\",\"zh_cn\":\"温森斯\",\"zh_hk\":\"溫森斯\",\"zh_tw\":\"萬塞訥\"},{\"id\":\"283\",\"zone\":\"Europe/Warsaw\",\"en\":\"Warsaw\",\"zh_cn\":\"华沙\",\"zh_hk\":\"華沙\",\"zh_tw\":\"華沙\"},{\"id\":\"284\",\"zone\":\"America/New_York\",\"en\":\"Washington D.C.\",\"zh_cn\":\"华盛顿\",\"zh_hk\":\"華盛頓\",\"zh_tw\":\"華盛頓\"},{\"id\":\"285\",\"zone\":\"America/Indiana/Winamac\",\"en\":\"Winamac\",\"zh_cn\":\"威纳马克\",\"zh_hk\":\"威納馬克\",\"zh_tw\":\"威納馬克\"},{\"id\":\"286\",\"zone\":\"America/Winnipeg\",\"en\":\"Winnipeg\",\"zh_cn\":\"温尼伯\",\"zh_hk\":\"溫尼伯\",\"zh_tw\":\"溫尼伯\"},{\"id\":\"287\",\"zone\":\"Europe/Warsaw\",\"en\":\"Wroclaw\",\"zh_cn\":\"弗罗茨瓦夫\",\"zh_hk\":\"弗次瓦夫\",\"zh_tw\":\"弗次瓦夫\"},{\"id\":\"288\",\"zone\":\"Europe/Zagreb\",\"en\":\"Zagreb\",\"zh_cn\":\"萨格勒布\",\"zh_hk\":\"薩格勒布\",\"zh_tw\":\"札格瑞布\"},{\"id\":\"289\",\"zone\":\"Europe/Zurich\",\"en\":\"Zurich\",\"zh_cn\":\"苏黎世\",\"zh_hk\":\"蘇黎世\",\"zh_tw\":\"蘇黎世\"}]}";
        HttpCityRes bean = AppController.getGson().fromJson(temp, HttpCityRes.class);
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
