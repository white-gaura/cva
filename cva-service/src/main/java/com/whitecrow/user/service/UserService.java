package com.whitecrow.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author whitecrow
 */
public interface UserService extends IService<User> {

    /**
     *
     * @param user
     * @return
     */
    long userRegister(UserRegisterRequest user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 更具标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    Boolean isAdmin(HttpServletRequest request) ;

    Boolean isAdmin(User userLoginUser);

    /**
     * 编辑距离算法查询用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
