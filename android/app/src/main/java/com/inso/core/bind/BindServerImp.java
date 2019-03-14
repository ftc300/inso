package com.inso.core.bind;

import android.content.Context;

import com.inso.core.HttpMgr;
import com.inso.plugin.tools.L;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/14
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindServerImp implements IBindServer {
    private Context mContext;
    private CountDownLatch mLatch ;
    private AtomicBoolean mServerReturn;


    public BindServerImp() {
    }

    public BindServerImp(Context context) {
        mContext = context;
    }

    @Override
    public  boolean searchInfo() {
        mServerReturn = new AtomicBoolean(false);
        mLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    L.d("searchInfo sleep 2s");
                    Thread.sleep(2000);
                    mServerReturn.set(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLatch.countDown();
            }
        }).start();
        try {
            L.d("await");
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        L.d("searchInfo return " + mServerReturn.get());
        return mServerReturn.get();
    }

    @Override
    public boolean addBindInfo() {
        mLatch = new CountDownLatch(1);
        mServerReturn = new AtomicBoolean(false);
        HttpMgr.getRequestQueue(mContext).add(HttpMgr.getRequest(mContext, "", new HttpMgr.IResponse<JSONObject>() {
            @Override
            public void onSuccess(final JSONObject obj) {
                mLatch.countDown();
                mServerReturn.set(true);
            }

            @Override
            public void onFail() {
                mLatch.countDown();
                mServerReturn.set(false);
            }
        }));
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mServerReturn.get();
    }

    @Override
    public boolean deleteBindInfo() {
        mLatch = new CountDownLatch(1);
        return true;
    }
}
