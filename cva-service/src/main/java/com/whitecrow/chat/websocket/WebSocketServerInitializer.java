package com.whitecrow.chat.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author WhiteCrow
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel){
        // 通过SocketChannel去获取对应的管道pipeline
        ChannelPipeline pipeline = channel.pipeline();
        // 通过管道，添加handler  HttpServerCodec是netty自己提供的助手类
        // 当请求到服务端时候，我们需要做解码，响应到客户端时候需要做编码
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 对写大数据流的支持
        pipeline.addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        // 对HttpMessage进行聚合 聚合成FullHttpRequest或FullHttpResponse
        // 几乎在netty的编程中，都会用到这个handler
        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(1024*64));
        // 以上用于支持http协议
        // websocket服务器处理的协议  并且用于指定给客户端连接访问的路由：/ws
        pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
        // 自定义的handler
        pipeline.addLast(new ChatHandler());
    }
}
