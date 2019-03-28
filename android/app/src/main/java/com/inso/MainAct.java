package com.inso;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.githang.statusbar.StatusBarCompat;
import com.inso.mall.MallFrg;
import com.inso.mine.MineFrg;
import com.inso.notify.NotifyFrg;
import com.inso.product.ProductFrg;
import com.inso.watch.baselib.base.BaseAct;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class MainAct extends BaseAct implements BottomNavigationBar.OnTabSelectedListener {


    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar mBottomNavigationBar;
    private ProductFrg mProductFrg;
    private MineFrg mMineFrg;
    private NotifyFrg mNotifyFrg;
    private MallFrg mMallFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        mProductFrg = ProductFrg.getInstance();
        mMineFrg = MineFrg.getInstance();
        mNotifyFrg = NotifyFrg.getInstance();
        mMallFrg = MallFrg.getInstance();
        InitNavigationBar();
        setDefaultFragment();

//        BindStatus n  = new BindStatus("VyU7MmlnEH-In4YpCOiFBzwNfIVA5c4f", "E3:E5:8A:96:EA:09");
//        HttpMgr.postStringRequest(this, BASE_URL + "device/check-device", n, new HttpMgr.IResponse<String>() {
//            @Override
//            public void onSuccess(final String obj) {
//                L.d("postStringRequest onSuccess " + obj);
//            }
//
//            @Override
//            public void onFail() {
//                L.d("postStringRequest onFail ");
//            }
//        });
    }

    private void InitNavigationBar() {
        TextBadgeItem badge = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColor("#F85959")
                .setAnimationDuration(200)
                .setText("18")
                .setHideOnSelect(false);
        mBottomNavigationBar.setTabSelectedListener(this);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_cp_inactive, "产品").setInactiveIconResource(R.mipmap.ic_cp).setActiveColorResource(R.color.inso_red).setInActiveColor("#E6000000"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_sc_inactive, "商城").setInactiveIconResource(R.mipmap.ic_sc).setActiveColorResource(R.color.inso_red).setInActiveColor("#E6000000"))
//                .addItem(new BottomNavigationItem(R.mipmap.ic_lb, "小喇叭").setInactiveIconResource(R.mipmap.ic_cp_active).setBadgeItem(badge))
                .addItem(new BottomNavigationItem(R.mipmap.ic_lb_inactive, "小喇叭").setInactiveIconResource(R.mipmap.ic_lb).setActiveColorResource(R.color.inso_red).setInActiveColor("#E6000000"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_wd_inactive, "我的").setInactiveIconResource(R.mipmap.ic_wd).setActiveColorResource(R.color.inso_red).setInActiveColor("#E6000000"))
                .setFirstSelectedPosition(0)
                .initialise();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, mProductFrg);
        transaction.commit();
    }

    @Override
    public void onTabSelected(int position) {
        Log.d("onTabSelected", "onTabSelected: " + position);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.fragment_container, mProductFrg);
                break;
            case 1:
                transaction.replace(R.id.fragment_container, mMallFrg);
                break;
            case 2:
                transaction.replace(R.id.fragment_container, mNotifyFrg);
                break;
            case 3:
                transaction.replace(R.id.fragment_container, mMineFrg);
                break;
            default:
                transaction.replace(R.id.fragment_container, mProductFrg);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {
        Log.d("onTabUnselected", "onTabUnselected: " + position);
    }

    @Override
    public void onTabReselected(int position) {
        Log.d("onTabReselected", "onTabReselected: " + position);
    }

}