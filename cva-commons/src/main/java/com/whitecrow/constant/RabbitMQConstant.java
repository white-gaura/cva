package com.whitecrow.constant;

/**
 * @author WhiteCrow
 */
public interface RabbitMQConstant {
    String RABBIT_LIKE_QUEUE ="whitecrow.user_like.processing";
    String RABBIT_VIEW_QUEUE ="whitecrow.user_view.processing";
    String RABBIT_CHAT_QUEUE="whitecrow.chat_broadcast.processing.%s";
}
