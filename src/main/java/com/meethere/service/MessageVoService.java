package com.meethere.service;

import com.meethere.entity.Message;
import com.meethere.entity.vo.MessageVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MessageVoService  {
    MessageVo returnMessageVoByMessageID(int messageID);//根据留言ID，将 Message 转换为 MessageVo
    List<MessageVo> returnVo(List<Message> messages);//将 Message 列表批量转换为 MessageVo 列表
}
