package com.inso.core;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/15
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class UserMgr {


    private UserMgr() {
    }

    public static UserMgr getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean isExpired(){
        return false;
    }


    private static class SingletonHolder {
        private static final UserMgr INSTANCE = new UserMgr();
    }
}
