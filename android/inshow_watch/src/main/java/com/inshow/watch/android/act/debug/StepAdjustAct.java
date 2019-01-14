package com.inshow.watch.android.act.debug;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.view.pieview.PieChart;
import com.inshow.watch.android.view.pieview.PieData;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import java.util.ArrayList;
import java.util.UUID;

import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 */
public class StepAdjustAct extends BasicAct {


    private PieChart pieChart;
    private ArrayList<PieData> mData = new ArrayList<>();
    private int[] mColors = new int[]{0XFF02B9AE, 0xFF6495ED, 0xFFE32636};

    @Override
    protected int getContentRes() {
        return R.layout.watch_debug_adjust_step;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText("步针调试");
        pieChart = (PieChart) findViewById(R.id.pieChart);
        initData();
        pieChart.setStartAngle(0);
        pieChart.setAnimatedFlag(false);
        pieChart.setClickListener(new PieChart.IClick() {
            @Override
            public void click(int i) {
                if(i== 0){
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(16), new Response.BleWriteResponse() {
                        @Override
                        public void onResponse(int code, Void data) {
                        }
                    });
                }else if(i==1){
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(4), new Response.BleWriteResponse() {
                        @Override
                        public void onResponse(int code, Void data) {
                        }
                    });
                }else  if(i==2){
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(1), new Response.BleWriteResponse() {
                        @Override
                        public void onResponse(int code, Void data) {
                        }
                    });
                }
            }
        });
        pieChart.setPieData(mData);
    }

    private void initData(){
            PieData pieData1 = new PieData();
            pieData1.setName("90度");
            pieData1.setValue(2);
            pieData1.setColor(mColors[0]);
            PieData pieData2= new PieData();
            pieData2.setName("20度");
            pieData2.setValue(1);
            pieData2.setColor(mColors[1]);
            PieData pieData3= new PieData();
            pieData3.setName("1步");
            pieData3.setValue(1);
            pieData3.setColor(mColors[2]);
            mData.add(pieData1);
            mData.add(pieData2);
            mData.add(pieData3);
    }
}
