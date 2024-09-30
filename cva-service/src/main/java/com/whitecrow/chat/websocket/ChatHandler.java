package com.whitecrow.chat.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.whitecrow.chat.singleton.NettySingleton;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.model.vo.UserVO;
import com.whitecrow.user.service.UserService;
import com.whitecrow.utils.RedisUtil;
import com.whitecrow.utils.SpringApplicationUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 处理消息的handler
 * TextWebSocketFrame：在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 * @author WhiteCrow
 */
@Component
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    // 用于记录和管理所有客户端的ChannelGroup
    private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final NettySingleton channelMap= NettySingleton.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)  {
        UserService  userService = SpringApplicationUtils.getBean(UserService.class);
        RabbitTemplate rabbitTemplate=SpringApplicationUtils.getBean(RabbitTemplate.class);
        // 获取客户端传输过来的消息
        //msg传输过来的是json,解析json获取到toUser,后查询redis,如果存在ctx发送过去,如果没有直接放到消息里缓存
        String content = msg.text();
        System.out.println("接收到的数据：" + content);
        JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
        Long userId= (jsonObject.get("id")).getAsLong();
        Long toUserId= (jsonObject.get("toId")).getAsLong();
        String message= String.valueOf(jsonObject.get("message"));
        ctx.writeAndFlush(new TextWebSocketFrame(content));
        User user=userService.getById(userId);
        User toUser=userService.getById(toUserId);
        UserVO userVO=new UserVO();
        UserVO toUserVO=new UserVO();
        BeanUtils.copyProperties(user,userVO);
        BeanUtils.copyProperties(toUser,toUserVO);
        Gson gson = new Gson();
        JsonObject jsonUser = gson.toJsonTree(userVO).getAsJsonObject();
        JsonObject jsonToUser = gson.toJsonTree(toUserVO).getAsJsonObject();
            //todo 用单例map缓存channel rabbitMQ生产消费
        if(channelMap.get(userId)==null){
            channelMap.put(userId, ctx);
        }
        JsonObject sendJsonObject = new JsonObject();
        sendJsonObject.add("id",jsonUser);
        sendJsonObject.add("toUser",jsonToUser);
        sendJsonObject.addProperty("message",message);
            //生产信息 msg,id
        Map<String,Object> msgMap=new HashMap<>();
        msgMap.put("id",toUserId);
        msgMap.put("msg",sendJsonObject);
        log.info("这里有长度---------------"+channelMap.getKeySet().toString());
        try {
            rabbitTemplate.convertAndSend( "netty.fanout","",msgMap);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "mq失败");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 当客户端连接服务端之后，获取客户端的channel，并且放到ChannelGroup中去进行管理
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 这步是多余的，当断开连接时候ChannelGroup会自动移除对应的channel
        clients.remove(ctx.channel());
    }
    public void handleMessage(ChannelHandlerContext ctx,Object msg) {
        ctx.writeAndFlush(msg);
    }
}