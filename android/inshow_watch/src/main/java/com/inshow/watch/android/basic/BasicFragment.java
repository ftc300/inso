package com.inshow.watch.android.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inshow.watch.android.provider.DBHelper;
import com.xiaomi.smarthome.device.api.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;

/**
 * Created by chendong on 2017/2/17.
 */

public class BasicFragment extends BaseFragment {

    protected Context mContext;
    /**
     * Fragment参数
     */
    private Bundle arguments;
    protected DBHelper mDBHelper;
    protected String MAC;
    protected String MODEL;
    protected String DID;
    protected String UID;

    //直接跳转
    protected  void switchTo(Class<?> to) {
        xmPluginActivity().startActivity(null, to.getName());
    }

    //EvebtBus的跳转
    protected  void switchToWithEventBus(Class<?> to) {
        Intent i = new Intent();
        Map<String, Object> map = new HashMap<>();
        map.put(EXTRAS_EVENT_BUS,true);
        putExtras(map, i);
        xmPluginActivity().startActivity(i, to.getName());
    }

    /**
     * intent 中 传递数据
     *
     * @param extras
     * @param i
     */
    private static void putExtras(Map<String, Object> extras, Intent i) {
        if (extras != null) {
            for (String name : extras.keySet()) {
                Object obj = extras.get(name);
                if (obj instanceof String) {
                    i.putExtra(name, (String) obj);
                }
                if (obj instanceof Integer) {
                    i.putExtra(name, (Integer) obj);
                }
                if (obj instanceof String[]) {
                    i.putExtra(name, (String[]) obj);
                }
                if (obj instanceof Boolean) {
                    i.putExtra(name, (Boolean)obj);
                }
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        arguments = getArguments();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDBHelper = new DBHelper(getActivity());
        MAC = mDBHelper.getCache(SP_ARG_MAC);
        MODEL = mDBHelper.getCache(SP_ARG_MODEL);
        UID = mDBHelper.getCache(SP_ARG_USERID);
        DID = mDBHelper.getCache(SP_ARG_DID);
        if (arguments != null && arguments.getBoolean(EXTRAS_EVENT_BUS, false)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arguments != null && arguments.getBoolean(EXTRAS_EVENT_BUS, false)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
