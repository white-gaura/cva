package com.whitecrow.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whitecrow.blog.model.domain.BlogLike;

import java.util.Map;

/**
* @author WhiteCrow
* @description 针对表【blog_like】的数据库操作Mapper
* @createDate 2023-11-02 13:37:10
* @Entity generator.domain.BlogLike
*/
public interface BlogLikeMapper extends BaseMapper<BlogLike> {
    BlogLike  selectBlogAndUser(Map<Long,Long> map);

    void deleteBlogAndUser(Map<Long,Long>map);

}




