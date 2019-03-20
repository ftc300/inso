package com.inso.watch.baselib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.inso.watch.commonlib.utils.StatusBarCompatUtil;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BaseAct extends AppCompatActivity{



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompatUtil.compat(this);
    }
}
