package com.inso;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.inso.mall.MallFrg;
import com.inso.mine.MineFrg;
import com.inso.notify.NotifyFrg;
import com.inso.plugin.act.mainpagelogic.PluginMainAct;
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
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);

        mProductFrg = ProductFrg.getInstance();
        mMineFrg = MineFrg.getInstance();
        mNotifyFrg = NotifyFrg.getInstance();
        mMallFrg = MallFrg.getInstance();
        InitNavigationBar();
        setDefaultFragment();
        addShortcut();
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
                .addItem(new BottomNavigationItem(R.mipmap.ic_cp, "产品").setActiveColorResource(R.color.black))
                .addItem(new BottomNavigationItem(R.mipmap.ic_sc, "商城").setActiveColorResource(R.color.black))
                .addItem(new BottomNavigationItem(R.mipmap.ic_lb, "小喇叭").setActiveColorResource(R.color.black).setBadgeItem(badge))
                .addItem(new BottomNavigationItem(R.mipmap.ic_wd, "我的").setActiveColorResource(R.color.black))
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
        // 事务提交
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

    //添加快捷方式
    private void addShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager scm = (ShortcutManager) getSystemService(SHORTCUT_SERVICE);
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, PluginMainAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ShortcutInfo si = new ShortcutInfo.Builder(this, "addShortcut")
                    .setIcon(Icon.createWithResource(this, R.drawable.watch_default))
                    .setShortLabel("米家石英表2")
                    .setIntent(launcherIntent)
                    .build();
            assert scm != null;
            scm.requestPinShortcut(si, null);
        } else {
            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");//"com.android.launcher.action.INSTALL_SHORTCUT"
            addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "米家石英表2");
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.watch_default));
            Intent launcherIntent = new Intent(getApplicationContext(), PluginMainAct.class);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
            sendBroadcast(addShortcutIntent);
        }
    }

}