package com.whitecrow.blog.service.impl;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootTest
public class BlogServiceImplTest {
@Resource
    RedisTemplate<String,Object> redisTemplate;
    @Test
    public void findRedis(){
        String prefix = "whitecrow1:blog:view:num:";
        redisTemplate.opsForSet().add(prefix+"123",123);
        redisTemplate.opsForSet().add(prefix+"234",234);
        Set<String> keys = redisTemplate.keys(prefix+"*");
        if (keys != null) {
            for (String key : keys) {
                System.out.println(key);
            }
        }


    }
}