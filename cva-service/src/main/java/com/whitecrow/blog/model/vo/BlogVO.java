package com.whitecrow.blog.model.vo;

import com.whitecrow.user.model.vo.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author WhiteCrow
 */
@Data
public class BlogVO implements Serializable {
    private static final long serialVersionUID = 7806071037642673665L;
    /**
     * 文章id
     */
    private Long id;

    /**
     * 用户id(作者)
     */
    private UserVO user;

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
     * 文章内容
     */
    private String content;

    /**
     * 点赞数量
     */
    private String likedNum;

    /**
     * 评论数量
     */
//    private String commentsNum;

    /**
     * todo 评论内容
     *
     */


    /**
     * 阅览数量
     */
    private String  viewsNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
