package com.whitecrow.blog.jobs;

import com.whitecrow.mapper.BlogMapper;
import com.whitecrow.model.domain.Blog;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.whitecrow.constant.RedisConstant.BLOG_READING_PERSON;
import static com.whitecrow.constant.RedissonConstant.VIEWS_NUM_LOCK_REDIS;

/**
 * @author WhiteCrow
 * redis内容定时持久化到mysql
 */
@Slf4j
public class RedisToMysqlForBlogJob {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    BlogMapper blogMapper;

    @Resource
    RedissonClient redissonClient;

    @Scheduled(cron = "0 0 0 ? * SAT")
    //同步阅读量
    public void redisToMysqlBlogViews() {
        log.info("开始阅读量redis同步到mysql");
        //分布式事务锁
        RLock lock = redissonClient.getLock(VIEWS_NUM_LOCK_REDIS);
        try {
            if (lock.tryLock(0, 2000L, TimeUnit.MICROSECONDS)) {
                String redisKey = String.format(BLOG_READING_PERSON, "*");
                Set<String> redisKeySet = redisTemplate.keys(redisKey);
                if (redisKeySet != null) {
                    for (String realKey : redisKeySet) {
                        Long blogViewNum = redisTemplate.opsForSet().size(realKey);
                        Long blogId = Long.parseLong(Arrays.stream(realKey.split(":"))
                                .filter(part -> part.matches("\\d+"))
                                .collect(Collectors.toList()).toString());
                        Long viewNumHistory = blogMapper.selectBlogViewsNum(blogId);
                        Blog blog = new Blog();
                        if (blogViewNum != null) {
                            blog.setViewsNum(blogViewNum + viewNumHistory);
                        }
                        blog.setId(blogId);
                        blogMapper.updateById(blog);
                    }
                    //删除查出来的键
                    redisTemplate.delete(redisKeySet);
                }
            }
        } catch (Exception e) {
            log.error("doRecommendUser error " + e);
        } finally {
            // 判断是不是自己上的锁，必需要在这里面写因为 try 里面的代码可能会出现 bug 导致不会解锁
            if (lock.isHeldByCurrentThread()) {
                // 解锁
                lock.unlock();
            }
        }
    }
    //todo 同步点赞表
}
