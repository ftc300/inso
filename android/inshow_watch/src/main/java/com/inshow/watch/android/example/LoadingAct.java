package com.inshow.watch.android.example;

import android.os.Bundle;
import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.MainAct;
import com.inshow.watch.android.act.user.BirthdayAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.NetWorkUtils;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.GifView;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.TOKEN_NOT_MATCHED;

/**
 * Created by chendong on 2017/2/17.
 */
public class LoadingAct extends BasicAct {
    private  GifView  gifView;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_loading;
    }

    @Override
    protected void initViewOrData() {
        setActStyle(ActStyle.GT);
        gifView = (GifView) findViewById(R.id.gifview);
        if (gifView.isPaused())
            gifView.play();
//        if(!isBTConnected()){//蓝牙没有连接
            connect();
//        }else{//如果蓝牙都保持连接的话 说明刚退出去不久 就不要执行任务了
//            swithActBySetBodyInfo();
//        }
    }

    private void connect()
    {
        XmBluetoothManager.getInstance().secureConnect(MAC, new Response.BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {//蓝牙连接成功了
//                    executorService.execute(new BtAsyncTask());
//                    if(NetWorkUtils.isNetworkConnected(mContext)) { //BT on & Net on
//                        executorService.execute(new NetAsyncTask());
//                    }else { //BT on & Net off
//                        ToastUtil.showToastNoRepeat(mContext,"网络异常..." );
//                    }
//                    switchIfisTerminated();
                    swithActBySetBodyInfo();
                }else if(code == TOKEN_NOT_MATCHED) {
                    XmBluetoothManager.getInstance().removeToken(MAC);
                    connect();
                } else {//BT off
                    ToastUtil.showToastNoRepeat(mContext,"蓝牙连接失败！");
//                    swithActBySetBodyInfo();
//                    if(!NetWorkUtils.isNetworkConnected(mContext)) {//Bt off & Net off
//                        ToastUtil.showToastNoRepeat(mContext,"网络异常..." );
//                        swithActBySetBodyInfo();
//                    }else{//Bt off & Net on
//                        executorService.execute(new NetAsyncTask());
//                        switchIfisTerminated();
//                    }
                }
            }
        });
    }


    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 蓝牙任务
     */
    class BtAsyncTask implements Runnable{
        @Override
        public void run() {
            L.e("BtAsyncTask execute");
        }
    }

    /**
     * 网路任务
     */
    class NetAsyncTask implements Runnable{
        @Override
        public void run() {
            L.e("NetAsyncTask execute");
        }
    }

    /**
     * 蓝牙是否连接
     * @return
     */
    private boolean isBTConnected(){
        return  XmBluetoothManager.getInstance().getConnectStatus(MAC) == XmBluetoothManager.STATE_CONNECTED;
    }

    /**
     * 网络是否连接
     * @return
     */
    private boolean isNetConnected(){
        return  NetWorkUtils.isNetworkConnected(mContext);
    }

    /**
     * 身体信息设置是否完成
     * @return
     */
    private boolean isBodyInfoCompleted()
    {
        return (int) SPManager.get(mContext,USER_REGISTER_KEY,0)> 0;
    }

    /**
     * 根据有没有设置完成身体信息来决定跳转的页面
     */
    private void swithActBySetBodyInfo(){
        if(isBodyInfoCompleted()) {
            switchToWithEventBus(MainAct.class);
        }else{
            switchTo(BirthdayAct.class);
        }
        finish();
    }

    /**
     * 任务执行结束后跳转
     */
    private  void  switchIfisTerminated() {
        executorService.shutdown();// 该方法在加入线程队列的线程执行完之前不会执行
        while (true) {
            //线程池任务执行结束
            if (executorService.isTerminated()) {
                if (gifView.isPlaying()) gifView.pause();
                swithActBySetBodyInfo();
                finish();
                //TODO:更新db sync key
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
