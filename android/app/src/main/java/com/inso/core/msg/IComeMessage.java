package com.inso.core.msg;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/2
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IComeMessage {
    /**
     * 短信来了
     */
    void  comeShortMessage(String msg);

    /**
     * 微信消息
     */
    void  comeWxMessage(String msg);

    /**
     * qq消息
     */
    void comeQQmessage(String msg);

}
