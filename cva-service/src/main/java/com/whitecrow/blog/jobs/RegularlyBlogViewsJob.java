package com.whitecrow.blog.jobs;


import com.whitecrow.blog.singleton.BlogSingleton;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.constant.RabbitMQConstant;
import com.whitecrow.exception.BusinessException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.whitecrow.constant.RabbitMQConstant.RABBIT_VIEW_QUEUE;
import static com.whitecrow.constant.RedisConstant.BLOG_READING_PERSON;


/**
 * @author WhiteCrow
 * 播放量并发量最高的
 */
public class RegularlyBlogViewsJob {


    @Resource
    RabbitTemplate rabbitTemplate;
    @Scheduled(cron = "0 * * * * *")
    public void regularlyBlogViews() {

        BlogSingleton blogSingleton = BlogSingleton.getInstance();
        for (Long blogId : blogSingleton.keyset()) {
            Set<String> blogSet = blogSingleton.get(blogId);
            Map<Long, String> rabbitQueue = new HashMap<>();
            for (String value : blogSet) {
                rabbitQueue.put(blogId, value);
                try {
                    rabbitTemplate.convertAndSend(RABBIT_VIEW_QUEUE, rabbitQueue);
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "rabbitMQ异常");
                }
            }
        }

        //定时任务将blogSingleton中的数据传入到redis
        //将单例模式中的数据传入rabbitmq中
        //消费者1、判断是否要添加播放量
        //     2、添加播放量到mysql
//        for (Long blogId : blogSingleton.keyset()) {
//            Set<String> userAndBlogSet = blogSingleton.get(blogId);
//            String redisKey = String.format(BLOG_READING_PERSON, blogId);
//            for (String value : userAndBlogSet) {
//                setOperations.add(redisKey, value);
//            }
            // 为键设置过期时间
//            redisTemplate.expire(redisKey, 8, TimeUnit.DAYS);
            //缓存预热同步数据到redis
//        }
    }

}
