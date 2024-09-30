package com.whitecrow.userteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whitecrow.user.model.domain.UserTeam;


/**
* @author WhiteCrow
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-09-22 00:58:38
* @Entity com.whitecrow.cva.model.domain.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {
 int selectNum(Long id);
}




