package com.whitecrow.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whitecrow.blog.async.UpdateBlogLike;
import com.whitecrow.blog.mapper.BlogMapper;
import com.whitecrow.blog.model.domain.Blog;
import com.whitecrow.blog.model.dto.BlogQuery;
import com.whitecrow.blog.model.requset.BlogUpdateRequest;
import com.whitecrow.blog.model.vo.BlogListVO;
import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.blog.service.BlogService;
import com.whitecrow.blog.singleton.BlogLikeSingleton;
import com.whitecrow.blog.singleton.BlogSingleton;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.common.PageRequest;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.model.vo.UserVO;
import com.whitecrow.user.service.UserService;
import com.whitecrow.utils.BeanCopyUtil;
import com.whitecrow.utils.NumUtil;
import com.whitecrow.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.whitecrow.constant.RedisConstant.*;

/**
 * @author WhiteCrow
 * @description 针对表【blog】的数据库操作Service实现
 * @createDate 2023-10-30 14:51:06
 */
@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    UserService userService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    NumUtil numUtil;
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
        log.info(redisUtil.hasKey(String.format(BLOG_LIST,"*")).toString());
        redisUtil.deleteObject(String.format(BLOG_LIST,"*"));
        return blog.getId();
    }

    @Override
    //删除博客
    // todo mq
    public Boolean deleteBlog(Long blogId, User loginUser, boolean isAdmin) {
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
        redisUtil.deleteObject(BLOG_LIST+"*");

        return this.removeById(blogId);
    }

    @Override
    //修改博客
    public Boolean updateBlog(User loginUser, BlogUpdateRequest blog, boolean isAdmin) {
        // todo mq
        Blog oldBlog = this.getById(blog.getId());
        if (oldBlog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博文不存在");
        }
        Long createUserId = oldBlog.getUserId();


        if (isAdmin || !createUserId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String title = blog.getTitle();
        String blogDescription = blog.getBlogDescription();
        String content = blog.getContent();
        if (StringUtils.isAnyBlank(title, content, blogDescription)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Blog newBlog = new Blog();
        BeanCopyUtil.beanCopy(blog, newBlog);
        redisUtil.deleteObject(BLOG_LIST);

        return this.updateById(newBlog);
    }

    @Override
    //查询博客（标签）
    //根据标题查询博客，根据作者查询博客，分页查询博客，模糊查询博客
    public List<BlogListVO> getBlogTitle(BlogQuery blogQuery) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //查询自己他人的文章
        if (blogQuery.getId() != null && blogQuery.getId() > 0) {
            queryWrapper.eq("user_id", blogQuery.getId());
        }

        //根据描述查询文章
        String searchText = blogQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).
                    or().like("blog_description", searchText);
        }
        Page<Blog> blogPages = this.page(new Page<>(blogQuery.getPageNum(), blogQuery.getPageSize()), queryWrapper);
        List<Blog> blogs = blogPages.getRecords();

        if (CollectionUtils.isEmpty(blogs)) {
            return new ArrayList<>();
        }
        List<BlogListVO> blogListVos = new ArrayList<>();
        for (Blog blog : blogs) {
            BlogListVO blogListVO = new BlogListVO();
            BeanCopyUtil.beanCopy(blog, blogListVO);
            blogListVos.add(blogListVO);
        }
        return blogListVos;
    }

    @Override
    public List<BlogListVO> getBlogVoPageList(PageRequest pageRequest) {
        Page<Blog> blogPages= redisUtil.getCacheObject(String.format(BLOG_LIST,pageRequest.getPageNum()));
        if(blogPages==null){
            blogPages = this.page(new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize()));
            redisUtil.setCacheObject(String.format(BLOG_LIST,pageRequest.getPageNum()),blogPages,30,TimeUnit.MINUTES);
        }
        List<Blog> blogs = blogPages.getRecords();
        List<BlogListVO> blogListVos = new ArrayList<>();
        for (Blog blog : blogs) {
            BlogListVO blogListVO = new BlogListVO();
            BeanCopyUtil.beanCopy(blog, blogListVO);
            blogListVO.setLikedNum(numUtil.blogNum(blog.getLikedNum()));
            blogListVO.setViewsNum(numUtil.blogNum(blog.getViewsNum()));
            blogListVos.add(blogListVO);
        }
        return blogListVos;
    }

    @Override
    //查询博客正文
    public BlogVO getContentById(Long blogId, User loginUser) {
        if (blogId == null || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果登录,阅读量添加到缓存中
        if (loginUser != null) {
            Long userId = loginUser.getId();
            BlogSingleton blogSingleton = BlogSingleton.getInstance();
            blogSingleton.addToSet(blogId, userId);
            System.out.println("--------------------" + blogSingleton.get(blogId));
        }

        //todo 热门文章缓存redis
        //从redis中获取博客正文
        if (redisUtil.hasKey(String.format(BLOG_HOT_ALL , blogId))) {
            String key=String.format(BLOG_HOT_ALL , blogId);
            BlogVO hotBlog = redisUtil.getCacheObject(key);
            UpdateBlogLike updateHotBlogLike=new UpdateBlogLike(blogId,key);
            threadPoolTaskExecutor.execute(updateHotBlogLike);
            return hotBlog;
        }
        //快速查询博客
        if (redisUtil.hasKey(String.format(BLOG_QUICK, blogId))) {
            String key=String.format(BLOG_QUICK, blogId);
            BlogVO blog = redisUtil.getCacheObject(String.format(BLOG_QUICK , blogId));
            UpdateBlogLike updateHotBlogLike=new UpdateBlogLike(blogId,key);
            threadPoolTaskExecutor.execute(updateHotBlogLike);
            return blog;
        }

        //从mysql中获取博客正文
        Blog blog = this.getById(blogId);
        if (blog == null) {
            return new BlogVO();
        }

        BlogVO blogVO = new BlogVO();
        BeanCopyUtil.beanCopy(blog, blogVO);
        //获取作者用户信息
        User writer = userService.getById(blog.getUserId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(writer, userVO);
        blogVO.setUser(userVO);

        //博客点赞量
        Long redisBlogLikeNum = redisUtil.getSetNumValue(String.format(BLOG_LIKE_PERSON ,blogId));
        String blogLikeNum = numUtil.blogNum(redisBlogLikeNum + blog.getLikedNum());
        blogVO.setLikedNum(blogLikeNum);
        log.info("blogLikeNum"+blogVO.getLikedNum());

        //博客阅读量
        Long redisBlogViewNum = redisUtil.getSetNumValue(String.format(BLOG_READING_PERSON , blogId));
        String blogViewNum = numUtil.blogNum( redisBlogViewNum + blog.getViewsNum());
        blogVO.setViewsNum(blogViewNum);
        //添加到redis
        redisUtil.setCacheObject(String.format(BLOG_QUICK,blogId),blogVO,10, TimeUnit.MINUTES);
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
    public Boolean likeBlog(User loginUser, Long blogId) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请登录后再点赞");
        }
        if (blogId == null || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客存在问题");
        }
        //todo 可优化
//        Blog blog = this.getById(blogId);
//        if (blog == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在");
//        }
        //1. hashIP 二级缓存
        Long userId=loginUser.getId();
        BlogLikeSingleton blogLikeSingleton = BlogLikeSingleton.getInstance();
        if(blogLikeSingleton.findToSet(blogId, userId)){
            blogLikeSingleton.deleteSet(blogId, userId);
            return false;
        }
        blogLikeSingleton.addToSet(blogId, userId);
//        2.rabbitMQ 二级缓存
//        Map<Long, Long> rabbitQueue = new HashMap<>();
//        rabbitQueue.put(blogId, loginUser.getId());
//        //  异步
//        try {
//            rabbitTemplate.convertAndSend(RABBIT_LIKE_QUEUE, rabbitQueue);
//        } catch (Exception e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "rabbitMQ异常");
//        }
//        3.单开服务器redis 二级缓存
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


