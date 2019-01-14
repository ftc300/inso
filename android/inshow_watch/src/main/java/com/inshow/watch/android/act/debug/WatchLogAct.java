//package com.inshow.watch.android.act.debug;
//
//import android.support.v4.content.ContextCompat;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.inshow.watch.android.R;
//import com.inshow.watch.android.act.adapter.WatchLogAdp;
//import com.inshow.watch.android.basic.BasicListAct;
//import com.inshow.watch.android.manager.BleManager;
//import com.inshow.watch.android.model.DebugLogEntity;
//import com.inshow.watch.android.sync.SyncDeviceHelper;
//import com.inshow.watch.android.tools.ToastUtil;
//import com.inshow.watch.android.view.InShowProgressDialog;
//import com.inshow.watch.android.view.tipview.TipItem;
//import com.inshow.watch.android.view.tipview.TipView;
//import com.xiaomi.smarthome.bluetooth.Response;
//import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_DEBUG_LOG;
//import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
//
///**
// * @ 创建者:   CoderChen
// * @ 时间:     2017/9/29
// * @ 描述:
// */
//public class WatchLogAct extends BasicListAct {
//    private List<DebugLogEntity> data = new ArrayList<>();
//    private WatchLogAdp adp;
//    private Button btnAdd;
//    private boolean continueRead = true;
//    private SyncDeviceHelper.BtCallback callback = new SyncDeviceHelper.BtCallback() {
//
//        @Override
//        public void onBtResponse(byte[] bytes) {
//            int[] i = BleManager.B2I_getHistoryStep(bytes);
//            if (i[0] != -1 && continueRead) {
//                data.add(new DebugLogEntity(i[0], i[1], i[2], i[3]));
//                SyncDeviceHelper.syncWatchDebug(MAC, callback);
//            } else {
//                dialogInstance.dismiss();
//                Collections.reverse(data);
//                adp = new WatchLogAdp(mContext, data);
//                mListView.setAdapter(adp);
//                addListHead();
//                adp.setListener(new WatchLogAdp.ITextViewClick() {
//                    @Override
//                    public void onClick1(View v, String s) {
//                        new TipView.Builder(WatchLogAct.this, v)
//                                .addItem(new TipItem(s, ContextCompat.getColor(mContext, R.color.watch_white), ContextCompat.getColor(mContext, R.color.primaryColor)))
//                                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.primaryColor))
//                                .setAnimType(TipView.TYPE_SLIDE)
//                                .setItemDuration(500)
//                                .create();
//                    }
//
//                    @Override
//                    public void onClick2(View v, String s) {
//
//                    }
//
//                    @Override
//                    public void onClick3(View v, String s) {
//                        new TipView.Builder(WatchLogAct.this, v)
//                                .addItem(new TipItem(s, ContextCompat.getColor(mContext, R.color.watch_white), ContextCompat.getColor(mContext, R.color.primaryColor)))
//                                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.primaryColor))
//                                .setAnimType(TipView.TYPE_VERTICLE)
//                                .setItemDuration(500)
//                                .create();
//                    }
//
//                    @Override
//                    public void onClick4(View v, String s) {
//                        new TipView.Builder(WatchLogAct.this, v)
//                                .addItem(new TipItem(s, ContextCompat.getColor(mContext, R.color.watch_white), ContextCompat.getColor(mContext, R.color.primaryColor)))
//                                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.primaryColor))
//                                .setAnimType(TipView.TYPE_VERTICLE)
//                                .setItemDuration(500)
//                                .create();
//                    }
//                });
//            }
//        }
//    };
//    private InShowProgressDialog dialogInstance;
//    private View headView;
//
//    private void addListHead() {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        headView = inflater.inflate(R.layout.watch_debug_log_item, null);
//        ((TextView) headView.findViewById(R.id.tv00)).setText("事件ID");
//        ((TextView) headView.findViewById(R.id.tv01)).setText("时间");
//        ((TextView) headView.findViewById(R.id.tv02)).setText("参数Ⅰ");
//        ((TextView) headView.findViewById(R.id.tv03)).setText("参数Ⅱ");
//        mListView.addHeaderView(headView);
//    }
//
////    @Override
////    protected String getLoadingTip() {
////        return "数据加载中";
////    }
//
//    @Override
//    protected void initViewOrData() {
//        setTitleText(getString(R.string.watch_log));
//        setBtnOnBackPress();
//        btnAdd = (Button) findViewById(R.id.add);
//        btnAdd.setVisibility(View.GONE);
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_DEBUG_LOG), new byte[]{0, 0, 0, 0}, new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
//                    SyncDeviceHelper.syncWatchDebug(MAC, callback);
//                }else{
//                    ToastUtil.showToastNoRepeat(mContext,getString(R.string.read_fail));
//                }
//            }
//        });
//
//        dialogInstance = new InShowProgressDialog(mContext);
//        dialogInstance.setCanceledOnTouchOutside(false);
//        dialogInstance.setmIsCancelable(true);
//        dialogInstance.setIndeterminate(true);
//        dialogInstance.setMessage(getString(R.string.loading_wait));
//        dialogInstance.setCancelIntercepter(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                continueRead = false;
//                dialogInstance.dismiss();
//            }
//        });
//        dialogInstance.show();
//    }
//}
