package com.whitecrow.user.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author whitecrow
 */
@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = -7447265631969350154L;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 校验密码
     */
    private String checkPassword;
    /**
     * 用户昵称
     */
    private String username;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 性别
     */
    private String gender;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 标签（json）
     */
    private String tag;

}
