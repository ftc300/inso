package com.inso.core.bind;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IServerResult {
   void onDeviceHaveBond();
   void onDeviceNotBond();
   void onBindSuccess();
   void onBindFail();
   void onUnBindSuccess();
   void onUnBindFail();
   void onException();
}
