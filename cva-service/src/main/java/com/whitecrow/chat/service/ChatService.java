package com.whitecrow.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whitecrow.chat.model.domain.Chat;
import com.whitecrow.chat.model.vo.ChatMessageVO;

import java.util.List;

/**
* @author WhiteCrow
* @description 针对表【chat】的数据库操作Service
* @createDate 2024-04-07 10:11:57
*/
public interface ChatService extends IService<Chat> {

    List<ChatMessageVO> getHistoryMessage(Long fromId, Long toId);
}
