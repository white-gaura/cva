package com.whitecrow.blog.model.requset;

import lombok.Data;

import java.io.Serializable;

/**
 * @author WhiteCrow
 */
@Data
public class BlogAddRequest implements Serializable {
    private static final long serialVersionUID = 6424018596600549287L;

    /**
     * 描述
     */
    private String blogDescription;
    /**
     * 标题
     */
    private String title;

    /**
     * 文章
     */
    private String content;
}
