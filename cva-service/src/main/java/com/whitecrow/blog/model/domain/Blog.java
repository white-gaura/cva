package com.whitecrow.blog.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author WhiteCrow
 * @TableName blog
 */
@TableName(value = "blog")
@Data
public class Blog implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片，最多9张，多张以","隔开
     */
    private String images;
    /**
     * 描述
     */
    @TableField("blog_description")
    private String blogDescription;
    /**
     * 文章
     */
    private String content;

    /**
     * 点赞数量
     */
    @TableField("liked_num")
    private Long likedNum;
    /**
     * 阅览数量
     */
    @TableField("views_num")
    private Long viewsNum;
    /**
     * 评论数量
     */
    @TableField("comments_num")
    private Long commentsNum;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     *是否删除
     */
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}