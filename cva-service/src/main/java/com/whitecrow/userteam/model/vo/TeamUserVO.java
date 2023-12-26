package com.whitecrow.userteam.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.whitecrow.user.model.vo.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author WhiteCrow
 */
@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = 8961297910391393948L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /**
     * 参加人用户信息
     */
    private List<UserVO> userTeamList;
}