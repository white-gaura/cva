package com.whitecrow.onlys;

import com.whitecrow.mapper.UserMapper;
import com.whitecrow.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 导入用户任务
 *
 * @author WhiteCrow
 */
//@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
  @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        System.out.println("goodgoodgood");
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假数据");
            user.setUserAccount("100000"+i);
            user.setAvatarUrl("https://img0.baidu.com/it/u=3010308328,3595901107&fm=253&fmt=auto&app=138&f=JPEG?w=502&h=500");
            user.setGender("男");
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTag("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
