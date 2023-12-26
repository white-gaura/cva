package com.whitecrow.blog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.model.domain.Blog;
import com.whitecrow.model.domain.User;

import java.util.List;

/**
 * @author WhiteCrow
 * @description 针对表【blog】的数据库操作Service
 * @createDate 2023-10-30 14:51:06
 */
public interface BlogService extends IService<Blog> {

    //添加博客
    Long addBlog(Blog blog, User loginUser);

    //删除博客
    void deleteBlog(Long blogId, User loginUser, boolean isAdmin);

    //修改博客
    void updateBlog(User loginUser,Blog blog, boolean isAdmin);

    //查询博客（标签）
    //根据标题查询博客，根据作者查询博客，分页查询博客，模糊查询博客
    List<Blog> getBlogTitle(String searchText, User user);

    //查询博客正文
    BlogVO getContentById(Long blogId, User loginUser);

    //redis缓存，MQ消息队列，开启多个消费端服务，redis消费端限流
    //先将数据写入内存，一段事件后一起上传（定时任务）
    boolean likeBlog(User loginUser, Long blogId);

}
