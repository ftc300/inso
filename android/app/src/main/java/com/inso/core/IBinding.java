package com.inso.core;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IBinding {
    void permisson(); // 位置服务才能使用蓝牙连接
    void netSuccess();//检测网络是否正常
    void bleSuccess(); // 检测蓝牙是否正常
    void searchTimeout(); //搜不到
    void bleValidating(); // 蓝牙端验证
    void serverValidating();//服务端验证
    void validateTimeout();//搜到不按键
    void validateSuccess();// 验证成功
    void validateFail();// 验证成功
}
