package com.whitecrow.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.blog.service.BlogService;
import com.whitecrow.blog.singleton.BlogSingleton;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.mapper.BlogMapper;
import com.whitecrow.model.domain.Blog;
import com.whitecrow.model.domain.User;
import com.whitecrow.user.model.vo.UserVO;
import com.whitecrow.user.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Stream;

import static com.whitecrow.constant.RabbitMQConstant.RABBIT_LIKE_QUEUE;
import static com.whitecrow.constant.RedisConstant.*;

/**
 * @author WhiteCrow
 * @description 针对表【blog】的数据库操作Service实现
 * @createDate 2023-10-30 14:51:06
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {
    @Resource
    UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    BlogMapper blogMapper;
    @Override
    //添加博客
    public Long addBlog(Blog blog, User loginUser) {
        // todo mq
        //博客检验
        blogContentVerification(blog, loginUser);

        blog.setUserId(loginUser.getId());
        boolean saveResult = this.save(blog);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "没有添加成功捏");
        }
        return blog.getId();
    }

    @Override
    //删除博客
    // todo mq
    public void deleteBlog(Long blogId, User loginUser, boolean isAdmin) {
        if (this.getById(blogId) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博文不存在");
        }
        if (isAdmin) {
            this.removeById(blogId);
        }

        Blog blog = this.getById(blogId);
        if (!loginUser.getId().equals(blog.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.removeById(blogId);
    }

    @Override
    //修改博客
    public void updateBlog(User loginUser, Blog blog, boolean isAdmin) {
// todo mq
        Blog oldBlog = this.getById(blog.getId());
        if (oldBlog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博文不存在");
        }
        Long createUserId = oldBlog.getUserId();
        if (!isAdmin && createUserId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String title = blog.getTitle();
        String blogDescription = blog.getBlogDescription();
        String content = blog.getContent();
        if (StringUtils.isAnyBlank(title, content, blogDescription)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.updateById(blog);
    }

    @Override
    //查询博客（标签）
    //根据标题查询博客，根据作者查询博客，分页查询博客，模糊查询博客
    public List<Blog> getBlogTitle(String searchText, User user) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //查询自己他人的文章
        if (user != null && user.getId() > 0) {
            queryWrapper.eq("userId", user.getId());
        }
        //根据描述查询文章
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).
                    or().like("blogDescription", searchText);
        }
        List<Blog> blogList = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(blogList)) {
            return new ArrayList<>();
        }
        //todo 分页查询
        return blogList;
    }

    @Override
    //查询博客正文
    public BlogVO getContentById(Long blogId, User loginUser) {
        if (blogId == null || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取博客正文
        Blog blog = this.getById(blogId);
        if (blog == null) {
            return new BlogVO();
        }

        BlogVO blogVO = new BlogVO();
        BeanUtils.copyProperties(blog, blogVO);
        //获取用户信息
        User writer = userService.getById(blog.getUserId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(writer, userVO);
        blogVO.setUser(userVO);

        String redisKeyBlogLike = String.format(BLOG_LIKE_PERSON, blogId);
        String redisKeyNum = String.format(BLOG_READING_VOLUME, blogId);
        // todo 查询评论
        // todo 350090转化为3.5w
        // 查询阅读量(历史阅读量，定期的阅读量)
        //从redis中查询
        //1、定期的阅读量
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        Long viewsNum = (Long) valueOperations.get(redisKeyNum);
        //2、历史阅读量(redis持久化到mysql)
        if(viewsNum==null){
          viewsNum= blogMapper.selectBlogViewsNum(blogId);
        }
        blogVO.setViewsNum(String.valueOf(viewsNum));
        //查询点赞数
        Long blogLikeNum = (Long) valueOperations.get(redisKeyBlogLike);
        if(blogLikeNum==null){
            blogLikeNum=blogMapper.selectBlogLikedNum(blogId);
        }
        blogVO.setLikedNum(String.valueOf(blogLikeNum));
        //查询是否点赞(用户已经登录)
        //如果用户登录了
        if (loginUser != null) {
            String sb = String.valueOf(blogId) +
                    loginUser.getId();
            //如果登录了，添加阅读量
            //添加阅览量,先写入内存
            BlogSingleton blogSingleton = BlogSingleton.getInstance();
            blogSingleton.addToSet(blogId, sb);
            //用户是否点赞
            Boolean isBlogLiked = redisTemplate.opsForSet().isMember(redisKeyBlogLike, loginUser.getId());
            blogVO.setIsLikeBlog(isBlogLiked);
        }
        //todo redis同步到mysql
        return blogVO;
    }

    //redis缓存，MQ消息队列，开启多个消费端服务，redis消费端限流
    //连续点赞，前端先聚合写入，延迟后判断传入后端
    //前端先通过后端获取点赞次数，获取用户点赞状态
    //用户已经点赞，再点一次传到后端，前端-1
    //用户没有点赞，点一次传到后端，前端+1
    //点赞一次触发一次点赞操作(读写分离)
    @Override
    //点赞操作
    public boolean likeBlog(User loginUser, Long blogId) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请登录后再点赞");
        }
        if (blogId == null || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客存在问题");
        }
        Blog blog = this.getById(blogId);
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在");
        }
        Map<Long, Long> rabbitQueue = new HashMap<>();
        rabbitQueue.put(blogId, loginUser.getId());
        // 写入mq
        try {
            rabbitTemplate.convertAndSend(RABBIT_LIKE_QUEUE, rabbitQueue);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "rabbitMQ异常");
        }
        return true;
    }
/*
  todo 历史记录
 */

    /**
     * 博客校验
     */
    public void blogContentVerification(Blog blog, User loginUser) {
        //博客内容不能为空
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客为空啊啊 啊");
        }
        //没登录不能发布博客
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请登录才能发布博客啊啊啊");
        }
        //博客题目需要大于两个字
        if (blog.getTitle().length() < 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客题目太短");
        }
        //博客描述不能小于10个字
        if (blog.getBlogDescription().length() < 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述再长一点吧！！");
        }
        //博客要有正文
        if (blog.getContent() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容没有哦");
        }

    }

}


