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
    String BLOG_READING_PERSON = "whitecrow:blog:view:person:%s";
    /**
     * 用户点赞
     */
    String BLOG_LIKE_PERSON="whitecrow:blog:like:person:%s";

    /**
     * 热门博客正文
     */
    String BLOG_HOT_ALL="whitecrow:blog:hot:all:%s";
    /**
     * 快速访问博客正文
     */
    String BLOG_QUICK="whitecrow:blog:quick:%s";
    /**
     * 聊天状态
     */
    String CHAT_USER_STORE="whitecrow:chat:userStore:%s";
    /**
     * 聊天记录
     */
    String CHAT_MESSAGE="whitecrow:chat:message:%s";
    /**
     * 博客列表
     */
    String BLOG_LIST="whitecrow:blog:list:%s";
    /**
     * 是否开启该聊天端口
     */
    String CHAT_IS_START="whitecrow:chat:start:%s";

}
