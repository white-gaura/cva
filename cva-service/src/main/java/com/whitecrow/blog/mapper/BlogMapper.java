package com.whitecrow.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whitecrow.blog.model.domain.Blog;

import java.util.Set;

/**
* @author WhiteCrow
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2023-10-30 14:51:06
* @Entity generator.domain.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {

    Long selectBlogViewsNum(Long blogId);
    Long selectBlogLikedNum(Long blogId);
    Set<Long> selectAllBlogId();


}




