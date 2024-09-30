package com.whitecrow.blog.controller;

import com.whitecrow.blog.model.dto.BlogQuery;
import com.whitecrow.blog.model.requset.BlogAddRequest;
import com.whitecrow.blog.model.requset.BlogUpdateRequest;
import com.whitecrow.blog.model.vo.BlogListVO;
import com.whitecrow.blog.model.vo.BlogVO;
import com.whitecrow.blog.service.BlogService;
import com.whitecrow.blog.singleton.BlogLikeSingleton;
import com.whitecrow.common.*;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.blog.model.domain.Blog;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static com.whitecrow.constant.UserConstant.ADMIN_ROLE;
import static com.whitecrow.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @author WhiteCrow
 */
@Slf4j
@RestController
@RequestMapping("/blog")
@Api(tags = "博客接口")
public class BlogController {

    @Resource
    UserService userService;

    @Resource
    BlogService blogService;

    /**
     * 添加
     *
     * @param blogAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBlog(@RequestBody BlogAddRequest blogAddRequest, HttpServletRequest request) {
        if (blogAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        User loginUser = userService.getLoginUser(request);
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogAddRequest, blog);
        Long blogId = blogService.addBlog(blog, loginUser);
        if (blogId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加失败");
        }
        return ResultUtils.success(blogId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBlog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = loginUser.getUserRole() == ADMIN_ROLE;
        long blogId = deleteRequest.getId();
        boolean result = blogService.deleteBlog(blogId, loginUser, isAdmin);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新
     *
     * @param blogUpdateRequest
     * @param request
     * @return
     */

    @PostMapping("/update")
    public BaseResponse<Boolean> updateBlog(@RequestBody BlogUpdateRequest blogUpdateRequest, HttpServletRequest request) {
        if (blogUpdateRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = loginUser.getUserRole() == ADMIN_ROLE;
        boolean result = blogService.updateBlog(loginUser, blogUpdateRequest, isAdmin);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 分页展示博客
     *
     * @param pageRequest
     * @return
     */
    @PostMapping("/recommend")
    public BaseResponse<List<BlogListVO>> recommendBlogList(@RequestBody PageRequest pageRequest) {
        List<BlogListVO> blogListVos = blogService.getBlogVoPageList(pageRequest);
        if (blogListVos == null) {
            blogListVos = new ArrayList<>();
        }
        return ResultUtils.success(blogListVos);
    }

    /**
     * 查询博客（标签）
     * 根据标题查询博客，根据作者查询博客，分页查询博客，模糊查询博客
     *
     * @param blogQuery
     * @return
     */
    @PostMapping("/tage/page")
    public BaseResponse<List<BlogListVO>> pageBlog(@RequestBody BlogQuery blogQuery) {
        if (blogQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }
        List<BlogListVO> blogListVos = blogService.getBlogTitle(blogQuery);
        if (blogListVos == null) {
            blogListVos = new ArrayList<>();
        }
        return ResultUtils.success(blogListVos);
    }

    /**
     * 查询文章正文
     *
     * @param blogId
     * @param request
     * @return
     */
    @GetMapping("/content")
    public BaseResponse<BlogVO> getContentById(HttpServletRequest request, Long blogId) {
        if (blogId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求参数为空");
        }
        User loginUser = getUser(request);
        BlogVO blogVO = blogService.getContentById(blogId, loginUser);
        return ResultUtils.success(blogVO);
    }

    private User getUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            return null;
        }
        return (User) userObj;
    }

    @GetMapping("/like")
    public BaseResponse<Boolean> blogLike(HttpServletRequest request, Long blogId) {
        if (blogId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = blogService.likeBlog(loginUser, blogId);
        log.info("点赞数量"+BlogLikeSingleton.getInstance().countToSet(blogId).toString());
        return ResultUtils.success(res);
    }
}
