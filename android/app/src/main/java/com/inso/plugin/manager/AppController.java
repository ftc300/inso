package com.inso.plugin.manager;

import android.app.Application;

import com.google.gson.Gson;
import com.inso.plugin.basic.BasicAct;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chendong on 2017/3/30.
 */
public class AppController extends Application {
    private List<BasicAct> mList = new LinkedList<>();
    private static AppController instance;
    public static Gson gson ;

    private AppController() {
    }

    public synchronized static AppController getInstance() {
        if (null == instance) {
            instance = new AppController();
        }
        return instance;
    }

    public synchronized static Gson getGson() {
        if (null == gson) {
            gson  = new Gson();
        }
        return gson;
    }

    // add Activity
    public void addActivity(BasicAct activity) {
        mList.add(activity);
    }
    // remove Activity
    public void removeActivity(BasicAct activity) {
        mList.remove(activity);
    }

    public void exit() {
        try {
            for (BasicAct activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
