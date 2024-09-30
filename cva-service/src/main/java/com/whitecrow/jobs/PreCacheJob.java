package com.whitecrow.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whitecrow.constant.RedisConstant;
import com.whitecrow.constant.RedissonConstant;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author WhiteCrow
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;
    //重点用户
    private final List<Long> impUserList= Collections.singletonList(1L);

    @Scheduled(cron = "0 31 0 * * *")
    public void doRecommendUser() {
        RLock lock = redissonClient.getLock(RedissonConstant.CACHE_PREHEATING_LOCK);
        try {
            if (lock.tryLock(0, 2000L, TimeUnit.MICROSECONDS)) {
                for (Long id : impUserList) {
                    // 这里需要设计一个 redis 的 key 这个 key 需要指定的详细一点，因为很多项目可能共用一个 redis
                    String redisKey = String.format(RedisConstant.USER_RECOMMENDATIONS, id);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    IPage<User> userIPage = (IPage<User>) valueOperations.get(redisKey);
                    // 如果没有缓存那么直接查询数据库
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // current – 当前页   pageSize – 每页显示条数
                    userIPage = userService.page(new Page<>(1, 20), queryWrapper);
                    try {
                        // 放入到 redis 中
                                // 这里就算是放入失败也不需要报异常信息
                        // redis 一定要设置过期时间
                        valueOperations.set(redisKey, userIPage, 10, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis set key error " + e);
                    }
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
