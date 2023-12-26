package com.whitecrow.blog.controller;

import com.whitecrow.blog.model.requset.BlogAddRequest;
import com.whitecrow.blog.service.BlogService;
import com.whitecrow.common.BaseResponse;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.common.ResultUtils;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.model.domain.Blog;
import com.whitecrow.model.domain.User;
import com.whitecrow.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author WhiteCrow
 */
@RestController
@RequestMapping("/blog")
@Api(tags = "博客接口")
public class BlogController {

    @Resource
    UserService userService;

    @Resource
    BlogService blogService;

    @PostMapping("/add")
    public BaseResponse<Long> addBlog(@RequestBody BlogAddRequest blogAddRequest, HttpServletRequest request){
        if(blogAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }
        User loginUser=userService.getLoginUser(request);
        Blog blog=new Blog();
        BeanUtils.copyProperties(blog,blogAddRequest);
        Long blogId=blogService.addBlog(blog,loginUser);
        if(blogId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"添加失败");
        }
        return ResultUtils.success(blogId);
    }


}
