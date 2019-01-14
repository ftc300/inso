package com.inshow.watch.android.fragment;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/11/21
 * @ 描述:
 */


public interface ICheckOnClick<T> {
    void ifItemOriginChecked(T t); //原来就是选中状态
    boolean onClickedAllowed();//看返回的个数是否超过10个了
    void onItemClick(T t); //点击
}
