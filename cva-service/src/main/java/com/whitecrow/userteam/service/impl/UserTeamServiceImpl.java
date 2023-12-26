package com.whitecrow.userteam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whitecrow.mapper.UserTeamMapper;
import com.whitecrow.model.domain.UserTeam;
import com.whitecrow.userteam.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author WhiteCrow
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-09-22 00:58:38
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




