package com.whitecrow.blog.jobs;

import com.whitecrow.mapper.BlogMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.whitecrow.constant.RedisConstant.BLOG_READING_VOLUME;
import static com.whitecrow.constant.RedissonConstant.VIEWS_NUM_LOCK_MYSQL;

/**
 * @author WhiteCrow
 * mysql定时将博客内容缓存到redis
 */
@Slf4j
public class MysqlToRedisForBlogJob {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    RedissonClient redissonClient;
    @Resource
    BlogMapper blogMapper;

    @Scheduled(cron = "0 0 0 ? * SUN")
    //mysql定时缓存博客阅读量
    public void mySqlToRedisBlogViews() {
        log.info("开始mysql阅读量缓存到redis");
        RLock lock = redissonClient.getLock(VIEWS_NUM_LOCK_MYSQL);
        try {
            if (lock.tryLock(0, 2000L, TimeUnit.MICROSECONDS)) {
                Set<Long> blogIdSet = blogMapper.selectAllBlogId();
                for (Long blogId : blogIdSet) {
                    Long blogViewsNum = blogMapper.selectBlogViewsNum(blogId);
                    String redisKey = String.format(BLOG_READING_VOLUME, blogId);
                    redisTemplate.opsForHash().put(redisKey, blogId, blogViewsNum);
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
}
