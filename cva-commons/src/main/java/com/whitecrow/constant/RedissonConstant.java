package com.whitecrow.constant;

/**
 * @author WhiteCrow
 */

public interface RedissonConstant {
    /**
     * 缓存预热
     */
    String CACHE_PREHEATING_LOCK = "whitecrow:precacheJob:doRecommendUser:lock";

    /**
     * 添加队伍
     */
    String JOIN_TEAM_LOCK = "whitecrow:join_team";
    /**
     * redis->mysql同步阅读量
     */
    String VIEWS_NUM_LOCK_REDIS = "whitecrow:views_num:getViewAndUpdate:redis:lock";
    /**
     * mysql->redis缓存阅读量
     */
    String VIEWS_NUM_LOCK_MYSQL="whitecrow:views_num:getViewAndUpdate:mysql:lock";
}



