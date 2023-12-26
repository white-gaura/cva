package com.whitecrow.blog.mq;

import com.whitecrow.mapper.BlogLikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import java.util.Map;
import java.util.Set;

import static com.whitecrow.constant.RabbitMQConstant.RABBIT_LIKE_QUEUE;
import static com.whitecrow.constant.RedisConstant.BLOG_LIKE_PERSON;


/**
 * @author WhiteCrow
 */
@Slf4j
public class RabbitAboutBlog {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 从rabbitMQ获取并处理消息(监听并处理)
     */
    @RabbitListener(queues = RABBIT_LIKE_QUEUE)
    public void listenWorkQueueAboutBlogLike(Map<Long, Long> blogLikeMap) {
        log.info("开始消费用户点赞");
        Set<Long> blogIdSet = blogLikeMap.keySet();
        for (Long blogId : blogIdSet) {
            String redisKeyPerson = String.format(BLOG_LIKE_PERSON, blogId);
            Boolean isUserLikeBlogRedis = redisTemplate.opsForSet()
                    .isMember(redisKeyPerson, blogLikeMap.get(blogId));
            //如果redis中存在
            if (Boolean.TRUE.equals(isUserLikeBlogRedis)) {
                //从redis中移除
                redisTemplate.opsForSet().remove(redisKeyPerson, blogLikeMap.get(blogId));
            } else {
                //todo 这一块可以再设计一下，加入另一个队列，慢查询持久化到mysql
                redisTemplate.opsForSet().add(redisKeyPerson, blogLikeMap.get(blogId));
            }
        }
    }
    @RabbitListener
    public void listenWorkQueueAboutBlogView(Map<Long, Long> blogViewMap){

    }
}
            //从redis中查
            //1.已点赞不能再点赞（再次点赞取消点赞）
            //2.存入用户id

            //todo 消费写入redis（为了开启多个消费者建议是把消费单独写出来进行线程操作）
            //todo 为了程序的可靠一定要自定义线程池
            //todo 定时任务持久化到mysql

            //todo 还要想一个问题，他怎么知道自己已经点赞过这个视频？（所以这个mq要快点，不适合从mysql中查）
            //todo 其实这个东西可以在历史记录中，（可以考虑烤炉）

//                Map<Long, Long> map = new HashMap<Long, Long>() {
//                    private static final long serialVersionUID = -3282819363546215938L;
//                    {
//                        put(blogId, blogLikeMap.get(blogId));
//                    }
//                };
//                BlogLike userLikeBlogMysql = blogLikeMapper.selectBlogAndUser(map);
//                if (userLikeBlogMysql == null) {
//                redisTemplate.opsForSet().add(redisKeyPerson, blogLikeMap.get(blogId));
//                } else {
//                    //如果mysql中存在
//                    blogLikeMapper.deleteBlogAndUser(map);
//                }
//            }

