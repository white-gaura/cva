package com.whitecrow.config;//package com.whitecrow.config;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import static com.whitecrow.constant.RabbitMQConstant.RABBIT_CHAT_QUEUE;
//
//@Configuration
//public class TopicRabbitConfig {
//    //绑定键
//
//    @Bean
//    public Queue queue() {
//        return new Queue("topicQueue",true);
//    }
//
//
//    @Bean
//    TopicExchange exchange() {
//        return new TopicExchange("topicExchange");
//    }
//
//    //将firstQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
//    // 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
//    @Bean
//    Binding bindingExchangeMessage() {
//        return BindingBuilder.bind(queue()).to(exchange()).with(RABBIT_CHAT_QUEUE+".#");
//    }
//
//}
