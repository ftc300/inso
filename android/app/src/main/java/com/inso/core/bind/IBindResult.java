package com.inso.core.bind;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IBindResult {
    void showNoPermission();
    void showNetError();
    void showBleError();
    void showNotFoundDevice();
    void showHasBond();
    void showBindTimeout();
    void showBindFail();
    void showBindSuccess();
}
