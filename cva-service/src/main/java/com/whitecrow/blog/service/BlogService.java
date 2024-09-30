package com.whitecrow.blog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.whitecrow.blog.model.dto.BlogQuery;
import com.whitecrow.blog.model.requset.BlogUpdateRequest;
import com.whitecrow.blog.model.vo.BlogListVO;
import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.common.PageRequest;
import com.whitecrow.blog.model.domain.Blog;
import com.whitecrow.user.model.domain.User;

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
    Boolean deleteBlog(Long blogId, User loginUser, boolean isAdmin);

    //修改博客
    Boolean updateBlog(User loginUser, BlogUpdateRequest blog, boolean isAdmin);

    //查询博客（标签）
    //根据标题查询博客，根据作者查询博客，分页查询博客，模糊查询博客
    List<BlogListVO> getBlogTitle(BlogQuery blogQuery);

    List<BlogListVO> getBlogVoPageList(PageRequest pageRequest);

    //查询博客正文
    BlogVO getContentById(Long blogId, User loginUser);

    //redis缓存，MQ消息队列，开启多个消费端服务，redis消费端限流
    //先将数据写入内存，一段事件后一起上传（定时任务）
    Boolean likeBlog(User loginUser, Long blogId);

}
