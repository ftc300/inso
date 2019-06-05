package com.inso.core.websocket;

import com.inso.plugin.tools.L;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/6/5
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class WebsocketMgr {
    public static WebSocket ws;
    static {
        init();
    }

    public static void  connect(){
        try {
            if(null!=ws)
            ws.connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(String msg){
        if(null!=ws&&ws.isOpen()){
            ws.sendText(msg);
        }
    }

    public static void disconnect(){
        if(null!=ws&&ws.isOpen()){
            ws.disconnect();
        }
    }


    static void init() {
        try {
             ws= new WebSocketFactory().createSocket("http://192.168.2.103:9502", 5000) //ws地址，和设置超时时间
                    .setFrameQueueSize(5)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            L.d("receive text:"+text);
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
             L.d("连接成功");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
             L.d("连接错误：" + exception.getMessage());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
             L.d("断开连接");
        }

    }
}
