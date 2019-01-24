package com.inso.watch.baselib.manager;

import retrofit2.Retrofit;

import static com.inso.watch.baselib.Constants.BASE_URL;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/24
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class InitMgr {


    private InitMgr() {
    }

    public static InitMgr getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final InitMgr INSTANCE = new InitMgr();
    }


    public void initRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
    }
}
