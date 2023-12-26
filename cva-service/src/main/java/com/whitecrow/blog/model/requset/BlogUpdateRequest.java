package com.whitecrow.blog.model.requset;

import lombok.Data;

import java.io.Serializable;

/**
 * @author WhiteCrow
 */
@Data
public class BlogUpdateRequest implements Serializable {
    private static final long serialVersionUID = -5751169670552226753L;
    /**
     * 博客id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String blogDescription;
    /**
     * 文章
     */
    private String content;
}
