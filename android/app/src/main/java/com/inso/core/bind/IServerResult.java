package com.inso.core.bind;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IServerResult {
    public void onDeviceStatusPositive();
    public void onDeviceStatusNegative();
    public void onBindSuccess();
    public void onBindFail();
    public void onUnBindSuccess();
    public void onUnBindFail();
}
