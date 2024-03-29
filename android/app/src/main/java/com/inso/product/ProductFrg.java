package com.inso.product;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.Utils;
import com.inso.core.basic.RecycleRefreshFrg;
import com.inso.core.bind.BindMgr;
import com.inso.core.bind.BindResultImp;
import com.inso.core.bind.IUnbindResult;
import com.inso.entity.event.ProductBus;
import com.inso.entity.http.DeviceList;
import com.inso.plugin.act.mainpagelogic.PluginMainAct;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.Constants;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class ProductFrg extends RecycleRefreshFrg<DeviceList> implements IUnbindResult {

    public static ProductFrg getInstance(){
        ProductFrg f = new ProductFrg();
        f.setArguments(enableEventBus());
        return f;
    }

    private List<DeviceList.ResultBean> data = new ArrayList<>();
    private BindMgr mBindMgr;
    private BindResultImp mBindUiHandleImp;

    @Subscribe
    public void onEventMainThread(ProductBus bus) {
        if(bus.bindResult){
            onRefresh();
        }
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        mBindUiHandleImp = new BindResultImp(mContext);
        mBindMgr = new BindMgr(mContext,mBindUiHandleImp);
        mBindMgr.setUnbindResult(this);
        setTitleR(false, "产品", R.drawable.icon_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAct.start(mContext,SelectDeviceFrg.class);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        L.d("ProductFrg recycle refresh resume");
    }

    @Override
    protected String getRequestUrl() {
        return BASE_URL + "device/list";
    }

    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    protected void dealWithFetchData(DeviceList list) {
        data = list.getResult();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CommonAdapter<DeviceList.ResultBean>(mContext, R.layout.item_user_products, data) {
            @Override
            protected void convert(final ViewHolder holder, final DeviceList.ResultBean item, int position) {
                Utils.showWebIcon(item.getLogo(),holder.getView(R.id.imgLogo),R.drawable.pic_product_default);
                final String mac = item.getMac();
                if(!TextUtils.isEmpty(mac)){
//                    BleMgr.getInstance().register(mac, new BleConnectStatusListener() {
//                        @Override
//                        public void onConnectStatusChanged(String mac, int status) {
//                            if (status == STATUS_CONNECTED) {
//                                setConnectedState((TextView) holder.getView(R.id.tvName),true);
//                            } else if (status == STATUS_DISCONNECTED) {
//                                setConnectedState((TextView) holder.getView(R.id.tvName),false);
//                            }
//                        }
//                    });
                }
                holder.setOnClickListener(R.id.card_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPManager.put(mContext, Constants.SystemConstant.SP_ARG_MAC, mac);
                        switchToWithEventBus(PluginMainAct.class);
                    }
                });
                holder.setOnLongClickListener(R.id.card_view, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(mContext).setTitle("解绑").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBindMgr.unBindDevice(item.getMac(),item.getSn());
                            }
                        }).show();
                        return false;
                    }
                });
            }
        };


    }

    @Override
    protected void showNoData() {
        if(null== data || data.size() == 0){
            mTvNoData.setText("还没有添加设备");
            mTvNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void unBindSuccess() {
        onRefresh();
    }

    @Override
    public void unBindFail() {
        ToastWidget.showFail(mContext,"解绑失败");
    }


    private void setConnectedState(TextView textView,boolean isConnected){
        textView.setBackgroundResource(isConnected?R.drawable.alertdialog_bg :R.drawable.bg_corner_gray);
    }
}
