package com.whitecrow.team.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whitecrow.team.model.domain.Team;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.team.model.dto.TeamQuery;
import com.whitecrow.team.model.request.TeamJoinRequest;
import com.whitecrow.team.model.request.TeamQuitRequest;
import com.whitecrow.team.model.request.TeamUpdateRequest;
import com.whitecrow.userteam.model.vo.TeamUserVO;

import java.util.List;


/**
* @author WhiteCrow
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-09-21 10:39:11
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /*
          1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
          2. 不展示已过期的队伍（根据过期时间筛选）
          3. 可以通过某个**关键词**同时对名称和描述查询
          4. **只有管理员才能查看加密还有非公开的房间**
          5. 关联查询已加入队伍的用户信息
          6. **关联查询已加入队伍的用户信息
         */
    /*
          1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
          2. 不展示已过期的队伍（根据过期时间筛选）
          3. 可以通过某个**关键词**同时对名称和描述查询
          4. **只有管理员才能查看加密还有非公开的房间**
          5. 关联查询已加入队伍的用户信息
          6. **关联查询已加入队伍的用户信息
         */

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO>listTeam(TeamQuery teamQuery, boolean isAdmin);

    /*
      1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
      2. 不展示已过期的队伍（根据过期时间筛选）
      3. 可以通过某个**关键词**同时对名称和描述查询
      4. **只有管理员才能查看加密还有非公开的房间**
      5. 关联查询已加入队伍的用户信息
      6. **关联查询已加入队伍的用户信息
     */

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);


    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);


    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(long id, User loginUser);

}
