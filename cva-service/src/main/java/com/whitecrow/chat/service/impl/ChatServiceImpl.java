package com.whitecrow.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.whitecrow.chat.mapper.ChatMapper;
import com.whitecrow.chat.model.domain.Chat;
import com.whitecrow.chat.model.vo.ChatMessageVO;
import com.whitecrow.chat.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author WhiteCrow
* @description 针对表【chat】的数据库操作Service实现
* @createDate 2024-04-07 10:11:57
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService {

    @Override
    public List<ChatMessageVO> getHistoryMessage(Long fromId,Long toId){
    return null;
    }

}




