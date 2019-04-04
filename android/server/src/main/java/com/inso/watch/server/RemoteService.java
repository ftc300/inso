package com.inso.watch.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/4
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    private Person mPerson;
    private String mUserName;
    private String mUserAge;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind Service success!");
        mPerson = new Person("Edward", "24");
        return new RemoteStub();
    }

    class RemoteStub extends IRemoteAidlInterface.Stub {

        @Override
        public void show(final Person p) throws RemoteException {
            Log.d(TAG,"received data :" + p.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,p.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public String getPersonUserName() throws RemoteException {
            mUserName = mPerson.getmUserName();
            Log.d(TAG, "Person mUserName = " + mUserName);
            return mUserName;
        }

        @Override
        public String getPersonUserAge() throws RemoteException {
            mUserAge = mPerson.getmUserAge();
            Log.d(TAG, "Person mUserAge = " + mUserAge);
            return mUserAge;
        }

        @Override
        public int add(int a, int b) throws RemoteException {
            Log.d(TAG, "Person mUserAge = " + mUserAge);
            return a + b;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}