package com.whitecrow.team.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 * @author WhiteCrow
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 912426200347102852L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
