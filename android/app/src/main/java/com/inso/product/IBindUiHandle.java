package com.inso.product;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IBindUiHandle {
    void showNoPermison();
    void showNetError();
    void showBleError();
    void showHasBond();
    void showBindTimeout();
    void showBindFail();
    void showBindSuccess();
}
