package com.inso.plugin.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inso.plugin.provider.DBHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.inso.plugin.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;

/**
 * Created by chendong on 2017/2/17.
 */

public class BasicFragment extends Fragment {

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
        Intent i = new Intent(mContext,to);
        startActivity(i);
    }

    //EvebtBus的跳转
    protected  void switchToWithEventBus(Class<?> to) {
        Intent i = new Intent(mContext,to);
        Map<String, Object> map = new HashMap<>();
        map.put(EXTRAS_EVENT_BUS,true);
        putExtras(map, i);
        startActivity(i);
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
