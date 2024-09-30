package com.whitecrow.chat.controller;

import com.whitecrow.chat.async.StartWs;
import com.whitecrow.chat.model.vo.ChatMessageVO;
import com.whitecrow.chat.service.ChatService;
import com.whitecrow.common.BaseResponse;
import com.whitecrow.common.ErrorCode;
import com.whitecrow.common.ResultUtils;
import com.whitecrow.exception.BusinessException;
import com.whitecrow.user.model.domain.User;
import com.whitecrow.user.service.UserService;
import com.whitecrow.chat.websocket.WebSocketServer;
import com.whitecrow.utils.RedisUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.whitecrow.constant.RedisConstant.CHAT_IS_START;

@Slf4j
@RestController
@RequestMapping("/chat")
@Api(tags = "聊天接口")
public class ChatController {
    @Resource
    WebSocketServer webSocketServer;
    @Resource
    UserService userService;
    @Resource
    ChatService chatService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping("/start")
    public BaseResponse<String> startWs(HttpServletRequest request) throws InterruptedException {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录请登录");
        }
        //后续可以扩展为请求轮询端口url
        String wsUrl = "127.0.0.1:8080/ws";
        String isStart = redisUtil.getCacheObject(String.format(CHAT_IS_START,wsUrl));
        if (Objects.equals(isStart, "1")) {
            log.info("以经开启了");
            return ResultUtils.success(wsUrl);
        }
        StartWs startWs=new StartWs("8080");
        threadPoolTaskExecutor.execute(startWs);
        redisUtil.setCacheObject(String.format(CHAT_IS_START , wsUrl),"1",3000,TimeUnit.MINUTES);
        log.info("开启成功了");
        return ResultUtils.success(wsUrl);
    }

    @PostMapping("/historyMessage")
    public BaseResponse<List<ChatMessageVO>> getHistoryMessage(HttpServletRequest request, Long toUserId) {
        if (toUserId == null && toUserId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必须有请求的人");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录请登录");
        }
        List<ChatMessageVO> messageList = chatService.getHistoryMessage(loginUser.getId(), toUserId);
        return ResultUtils.success(messageList);
    }
}
