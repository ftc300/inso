package com.inshow.watch.android.sync;

import android.content.Context;

import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.tasks.AlarmTask;
import com.inshow.watch.android.sync.http.tasks.ConfigFileDownloadTask;
import com.inshow.watch.android.sync.http.tasks.IntervalTask;
import com.inshow.watch.android.sync.http.tasks.StepHistoryTask;
import com.inshow.watch.android.sync.http.tasks.UserInfoTask;
import com.inshow.watch.android.sync.http.tasks.VibrateTask;
import com.inshow.watch.android.sync.http.tasks.VipTask;
import com.inshow.watch.android.sync.http.tasks.WorldCityTask;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chendong on 2017/5/19.
 */
public class AsyncHttpManager {
    private ScheduledExecutorService httpExecutorService;
    private Context context;
    private DBHelper mDBHelper;

    public AsyncHttpManager(Context context, DBHelper mDBHelper) {
        this.mDBHelper = mDBHelper;
        this.context = context;
        httpExecutorService = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }

    public void startHttpSync(ITerminatedListener listener) {
        if(!isShutDown()) {
            httpExecutorService.schedule(new VipTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new ConfigFileDownloadTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new StepHistoryTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new UserInfoTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new IntervalTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new AlarmTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new WorldCityTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.schedule(new VibrateTask(context, mDBHelper), 0, TimeUnit.MILLISECONDS);
            httpExecutorService.shutdown();// will not execute before tasks complete
            //TODO:wait for result?
            listener.onAsyncFinished();
        }
    }

    public void releaseHttpAsyncManager() {
        if (null != httpExecutorService && !httpExecutorService.isShutdown()) {
            httpExecutorService.shutdownNow();
        }
    }

    private boolean isShutDown(){
        if (httpExecutorService == null) {
            return true;
        }
       return httpExecutorService.isShutdown();
    }

}
