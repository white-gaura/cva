package com.whitecrow.blog.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author WhiteCrow
 */
@Data
public class BlogListVO implements Serializable {
    private static final long serialVersionUID = 7806071037642673665L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String blogDescription;

    /**
     * 点赞数量
     */
    private String likedNum;

    /**
     * 评论数量
     */
//    private String commentsNum;

    /**
     * 阅读数量
     */
    private String viewsNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
