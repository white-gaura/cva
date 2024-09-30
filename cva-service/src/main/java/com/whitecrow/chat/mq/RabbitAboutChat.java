package com.whitecrow.chat.mq;

import com.google.gson.JsonObject;
import com.whitecrow.chat.singleton.NettySingleton;
import com.whitecrow.chat.websocket.ChatHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import javax.annotation.Resource;
import java.util.Map;


/**
 * @author WhiteCrow
 */
@Slf4j
public class RabbitAboutChat {

    /**
     * 从rabbitMQ获取并处理消息(监听并处理)
     */
    @Resource
    ChatHandler chatHandler;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "fanout.queue1"),
            exchange = @Exchange(name = "netty.fanout",type = ExchangeTypes.FANOUT),
            key = {""}
    ))
    public void listenWorkQueueAboutChat(Map<String,Object> msgMap) throws Exception {
        Long userId= (Long) msgMap.get("userId");
        JsonObject msg= (JsonObject) msgMap.get("msg");
        ChannelHandlerContext ctx = (ChannelHandlerContext) NettySingleton.getInstance().get(userId);
        chatHandler.handleMessage(ctx,msg);
    }
}
