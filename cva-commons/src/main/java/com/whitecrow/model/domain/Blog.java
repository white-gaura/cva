package com.whitecrow.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
    private String blogDescription;
    /**
     * 文章
     */
    private String content;

    /**
     * 点赞数量
     */
    private Long likedNum;
    /**
     * 阅览数量
     */
    private Long viewsNum;
    /**
     * 评论数量
     */
    private Long commentsNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}