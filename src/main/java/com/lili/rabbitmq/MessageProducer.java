package com.lili.rabbitmq;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer{
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 向队列发送消息
     * @param exchange 交换机
     * @param routingKey 根据routingKey转发消息到指定的队列
     * @param message 消息内容
     */
    public void sendMessage(String exchange, String routingKey, String message){
        log.info("消息队列发送判题消息: {}", message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
