package com.whitecrow.blog.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whitecrow.blog.service.BlogLikeService;
import com.whitecrow.mapper.BlogLikeMapper;
import com.whitecrow.model.domain.BlogLike;
import org.springframework.stereotype.Service;

/**
* @author WhiteCrow
* @description 针对表【blog_like】的数据库操作Service实现
* @createDate 2023-11-02 13:37:10
*/
@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike>
    implements BlogLikeService {

}




