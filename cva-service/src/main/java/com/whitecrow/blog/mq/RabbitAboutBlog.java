package com.whitecrow.blog.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Map;

import static com.whitecrow.constant.RabbitMQConstant.RABBIT_LIKE_QUEUE;


/**
 * @author WhiteCrow
 */
@Slf4j
public class RabbitAboutBlog {

    /**
     * 从rabbitMQ获取并处理消息(监听并处理)
     */
    @RabbitListener(queues = RABBIT_LIKE_QUEUE)
    public void listenWorkQueueAboutBlogLike(Map<Long, Long> blogLikeMap) {
    }
    @RabbitListener(queues = RABBIT_LIKE_QUEUE)
    public void listenWorkQueueAboutBlogView(Map<Long, Long> blogViewMap){

    }
}
