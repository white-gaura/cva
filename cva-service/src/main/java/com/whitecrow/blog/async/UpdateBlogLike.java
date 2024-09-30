package com.whitecrow.blog.async;

import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.blog.singleton.BlogLikeSingleton;
import com.whitecrow.utils.NumUtil;
import com.whitecrow.utils.RedisUtil;
import com.whitecrow.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;

import java.util.Set;

import static com.whitecrow.constant.RedisConstant.*;


/**
 * @author WhiteCrow
 */
@Slf4j
public class UpdateBlogLike implements Runnable {


    private final Long blogId;
    private final String key;
    private int index = 0;

    public UpdateBlogLike(Long blogId, String key) {
        this.blogId = blogId;
        this.key = key;
    }

    @Override
    public void run() {
        log.info("进入到更新点赞/阅读量线程");
        log.info("更新点赞/阅读量线程任务开始");
        RedisUtil redisUtil = SpringApplicationUtils.getBean(RedisUtil.class);
        NumUtil numUtil = SpringApplicationUtils.getBean(NumUtil.class);
        log.info("开始缓存到redis");
        BlogLikeSingleton blogLikeSingleton=BlogLikeSingleton.getInstance();
        Set<Long> keySet=blogLikeSingleton.getKeySet();
        for(Long key:keySet){
            redisUtil.setCacheSet(String.format(BLOG_LIKE_PERSON,key),blogLikeSingleton.get(key));
        }
        blogLikeSingleton.clear();
        if (redisUtil.hasKey(key)) {
            BlogVO blog = redisUtil.getCacheObject(key);
            log.info("获取到博客");
            blog.setLikedNum(numUtil.blogNum(numUtil.reverseBlogNum(blog.getLikedNum()) + redisUtil.getSetNumValue(String.format(BLOG_LIKE_PERSON ,blogId))));
            blog.setViewsNum(numUtil.blogNum(numUtil.reverseBlogNum(blog.getViewsNum() )+ redisUtil.getSetNumValue(String.format(BLOG_READING_PERSON , blogId))));
            redisUtil.updateObject(key, blog);
            redisUtil.deleteObject(String.format(BLOG_LIKE_PERSON,blogId));
            log.info("更新成功点赞数量"+ blog.getLikedNum());
        }
    }
}

