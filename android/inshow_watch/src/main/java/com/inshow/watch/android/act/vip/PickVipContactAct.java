package com.inshow.watch.android.act.vip;

import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.adapter.ContactAdapter;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.ChangeUI;
import com.inshow.watch.android.fragment.ContactSearchFragment;
import com.inshow.watch.android.fragment.ICheckOnClick;
import com.inshow.watch.android.model.PickVipEntity;
import com.inshow.watch.android.model.VipEntity;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.MessUtil;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.InShowProgressDialog;
import com.inshow.watch.android.view.indexable.EntityWrapper;
import com.inshow.watch.android.view.indexable.IndexableAdapter;
import com.inshow.watch.android.view.indexable.IndexableLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.inshow.watch.android.event.ChangeUI.RENDER_AGAIN;

/**
 * Created by chendong on 2017/1/22.
 * 选择联系人
 */
public class PickVipContactAct extends BasicAct {

    private Button buttonOK;
    private TextView selectAllTitle;
    private FrameLayout flSelectAll;
    private EditText searchEt;
    private IndexableLayout indexableLayout;
    private FrameLayout mProgressBar;
    private FrameLayout flList;
    private ContactAdapter mAdapter;
    private ArrayList<PickVipEntity> mDataSrc = new ArrayList<>();
    private List<VipEntity> mAddData = new ArrayList<>();
    private ContactSearchFragment mSearchFragment;
    private int size;
    private InShowProgressDialog dialogInstance;
    private int mLimitTime = 2000;
    private List<VipEntity> dataSource;
    private HashSet<Integer> sets = new HashSet<>();
    private List<Integer> diffIds;
    private Timer timer;
    private boolean isProcessing = true;
    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_pickcontact;
    }

    @Override
    protected void initViewOrData() {
        L.e("PickVipContactAct:initViewOrData");
        setBtnOnBackPress(R.id.select_all_cancel);
        setActStyle(ActStyle.BT);
        dataSource = mDBHelper.getVipContact();
        diffIds = getDiffIds();
        size = dataSource.size();
        mSearchFragment = ContactSearchFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.vip_search_fragment, mSearchFragment)
                .commitAllowingStateLoss();
        indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        flList = (FrameLayout) findViewById(R.id.fl_list);
        mTitleView.setVisibility(View.GONE);
        flSelectAll.setVisibility(View.VISIBLE);
        buttonOK = (Button) findViewById(R.id.select_all_select);
        selectAllTitle = (TextView) findViewById(R.id.select_all_title);
        searchEt = (EditText) findViewById(R.id.et_search);
        mProgressBar = (FrameLayout) findViewById(R.id.progress);
        buttonOK.setText(R.string.button_ok);
        selectAllTitle.setText(R.string.please_select_contact);
        timer = new Timer();
        mSearchFragment.setListener(new ICheckOnClick<PickVipEntity>() {
            @Override
            public void ifItemOriginChecked(PickVipEntity pickVipEntity) {
                removeSets(pickVipEntity);
            }

            @Override
            public boolean onClickedAllowed() {
                addSets(mSearchFragment.mSelDatas);
                addSets(mDataSrc);
                if (sets.size() + size > 9) {
                    ToastUtil.showToastNoRepeat(mContext, getString(R.string.contact_most_tip));
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onItemClick(PickVipEntity pickVipEntity) {
                modifySelectList(pickVipEntity);
            }

        });
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(searchEt.getText())) {
                    final int nowStart = (int) System.currentTimeMillis();
                    int size = mAddData.size();
                    if (size == 0) {
                        finish();
                        return;
                    }
                    //Fixme:暂定写入一个要500ms
                    final int todoPeriod = 500 * size;
                    for (VipEntity vipEntity : mAddData) {
                        if (mDBHelper.addVipContact(vipEntity)) {
                            SyncDeviceHelper.syncDeviceVip(MAC, vipEntity);
                        }
                    }
                    dialogInstance = new InShowProgressDialog(mContext);
                    dialogInstance.setCancelable(false);
                    dialogInstance.setCanceledOnTouchOutside(false);
                    dialogInstance.setIndeterminate(false);
                    dialogInstance.setMessage(getString(R.string.setting_waiting));
                    dialogInstance.setMax(todoPeriod >= mLimitTime ? todoPeriod : mLimitTime);
                    dialogInstance.show();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(isProcessing) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int delta = (int) (System.currentTimeMillis() - nowStart);
                                        if (null != dialogInstance) {
                                            if (delta <= (todoPeriod >= mLimitTime ? todoPeriod : mLimitTime)) {
                                                dialogInstance.setProgress(delta);
                                            } else {
                                                dialogInstance.setMessage(getString(R.string.setting_complete));
                                                isProcessing = false;
                                                finish();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }, 0, 50);
                } else {//search frg
                    searchEt.setText("");
                    searchEt.clearFocus();
                    if (mSearchFragment.mSelDatas.size() > 0) {
                        for (PickVipEntity selItem : mSearchFragment.mSelDatas) {
                            for (PickVipEntity srcItem : mDataSrc) {
                                if (selItem.number.equals(srcItem.number) && selItem.name.equals(srcItem.number)) {
                                    srcItem.status = selItem.status;
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        indexableLayout.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactAdapter(this);
        indexableLayout.setAdapter(mAdapter);
        readContactInitData();
        mAdapter.setDatas(mDataSrc);
        indexableLayout.setCompareMode(IndexableLayout.MODE_CONTACT);
        mAdapter.setDatas(mDataSrc, new IndexableAdapter.IndexCallback<PickVipEntity>() {
            @Override
            public void onFinished(List<EntityWrapper<PickVipEntity>> datas) {
                mSearchFragment.bindDatas(mDataSrc);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        indexableLayout.setOverlayStyle_Center();
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<PickVipEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, PickVipEntity entity) {
                if (!entity.status && mAddData.size() + size > 9) {
                    ToastUtil.showToastNoRepeat(mContext, getString(R.string.contact_most_tip));
                    return;
                } else if (entity.status) {
                    removeSets(entity);
                }
                entity.status = !entity.status;
                modifySelectList(entity);
                mAdapter.notifyDataSetChanged();
            }
        });
        initSearch();
    }

    private void modifySelectList(PickVipEntity entity) {
        if (entity.status) {
            L.e("modifySelectList 0");
            VipEntity item = new VipEntity(entity.contactId, diffIds.get(0), entity.number, entity.name, true);
            L.e(item.toString());
            mAddData.add(item);
            diffIds.remove(0);
        } else {
            L.e("modifySelectList 1");
            Iterator<VipEntity> iterator = mAddData.iterator();
            while(iterator.hasNext()){
                VipEntity item = iterator.next();
                if (item.contactId == entity.contactId) {
                    iterator.remove();
                    diffIds.add(item.id);
                }
            }
//            for (VipEntity item : mAddData) {
//                if (item.contactId == entity.contactId) {
//                    delList.add(item);
//                    mAddData.remove(item);
//                    diffIds.add(item.id);
//                }
//            }
        }
    }

    private void removeSets(PickVipEntity entity) {
        sets.remove(entity.contactId);
    }

    private void addSets(List<PickVipEntity> src) {
        for (PickVipEntity item : src) {
            if (item.status) {
                sets.add(item.contactId);
            }
        }
    }

    /**
     * 获得要添加的联系人数据
     *
     * @return
     */
    private List<VipEntity> getAddSrc() {
        List<Integer> diffIds = getDiffIds();
        for (PickVipEntity item : mDataSrc) {
            if (item.status) {
                mAddData.add(new VipEntity(item.contactId, diffIds.get(0), item.number, item.name, true));
                diffIds.remove(0);
            }
        }
        return mAddData;
    }

    /**
     * 去除数据库已有ID
     *
     * @return
     */
    private List<Integer> getDiffIds() {
        List<Integer> idSrc = getVipIDs();
        List<Integer> tableIDSrc = new ArrayList<>();
        List<VipEntity> tableSrc = mDBHelper.getVipContact();
        for (VipEntity item : tableSrc) {
            tableIDSrc.add(item.id);
        }
        idSrc.removeAll(tableIDSrc);
        return idSrc;
    }

    private void readContactInitData() {
        mDataSrc = MessUtil.getSystemContact(mContext);
        final List<PickVipEntity> list = new ArrayList<>();
        for (VipEntity entity : dataSource) {
            list.add(new PickVipEntity(entity.contactId, entity.number, entity.name, false));
        }
        mDataSrc.removeAll(list); //原有系统联系人去掉已经选择的联系人
    }

    @Override
    protected ActStyle getActStyle() {
        return ActStyle.GT;
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
                mSearchFragment.notifyDataChanged();
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

    /**
     * 初始化原始10个VipID
     *
     * @return
     */
    private List<Integer> getVipIDs() {
        List<Integer> vipIDSource = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            vipIDSource.add(2 * i - 1);
        }
        return vipIDSource;
    }

    @Override
    public void onDestroy() {
        L.e("PickVipContactAct:onDestroy");
        EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
    }
}
