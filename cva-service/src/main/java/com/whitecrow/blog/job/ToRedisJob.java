package com.whitecrow.blog.job;

import com.whitecrow.blog.singleton.BlogLikeSingleton;
import com.whitecrow.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;


@Slf4j
public class ToRedisJob {
    @Resource
    RedisUtil redisUtil;

    @Scheduled(fixedRate = 1000)
    public void toRedisJob(){
        log.info("开始缓存到redis");
        BlogLikeSingleton blogLikeSingleton=BlogLikeSingleton.getInstance();
        Set<Long> keySet=blogLikeSingleton.getKeySet();
        for(Long key:keySet){
            redisUtil.setCacheSet(key.toString(),blogLikeSingleton.get(key));
        }
        blogLikeSingleton.clear();
    }
}
