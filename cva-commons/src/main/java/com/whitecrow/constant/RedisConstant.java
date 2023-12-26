package com.whitecrow.constant;

/**
 * @author WhiteCrow
 */
public interface RedisConstant {
    /**
     * 用户推荐
     */
    String USER_RECOMMENDATIONS = "whitecrow:user:recommend:%s";
    /**
     * 博客阅读量
     */
    String BLOG_READING_VOLUME = "whitecrow:blog:view:num:%s";
    /**
     * 博客阅读人
     */
    String BLOG_READING_PERSON = "whitecrow:blog:view:person:%s";
    /**
     * 用户点赞
     */
    String BLOG_LIKE_PERSON="whitecrow:blog:like:person:%s";
    /**
     * 用户点赞量
     */
    String BLOG_LIKE_NUM="whitecrow:blog:like:person:%s";

}
