package com.inso;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.githang.statusbar.StatusBarCompat;
import com.inso.mall.MallFrg;
import com.inso.mine.MineFrg;
import com.inso.notify.NotifyFrg;
import com.inso.product.ProductFrg;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class MainAct extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {


    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar mBottomNavigationBar;
    private ProductFrg mProductFrg;
    private MineFrg mMineFrg;
    private NotifyFrg mNotifyFrg;
    private MallFrg mMallFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        mProductFrg = ProductFrg.getInstance();
        mMineFrg = MineFrg.getInstance();
        mNotifyFrg = NotifyFrg.getInstance();
        mMallFrg = MallFrg.getInstance();
        InitNavigationBar();
        setDefaultFragment();
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
                .addItem(new BottomNavigationItem(R.mipmap.ic_cp, "产品").setActiveColorResource(R.color.watch_red))
                .addItem(new BottomNavigationItem(R.mipmap.ic_sc, "商城").setActiveColorResource(R.color.watch_red))
                .addItem(new BottomNavigationItem(R.mipmap.ic_lb, "小喇叭").setActiveColorResource(R.color.watch_red).setBadgeItem(badge))
                .addItem(new BottomNavigationItem(R.mipmap.ic_wd, "我的").setActiveColorResource(R.color.watch_red))
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