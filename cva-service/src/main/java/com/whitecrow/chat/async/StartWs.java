package com.whitecrow.chat.async;

import com.whitecrow.chat.websocket.WebSocketServer;
import com.whitecrow.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * @author WhiteCrow
 */
@Slf4j
public class StartWs implements Runnable {

    private final String post;

    public StartWs(String post) {
        this.post = post;
    }


    @Override
    public void run() {
        WebSocketServer webSocketServer=  SpringApplicationUtils.getBean(WebSocketServer.class);
        try {
            webSocketServer.startWebSocket(post);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

