package com.lili.rabbitmq;

import com.lili.judge.JudgeService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MessageConsumer{

    @Resource
    private JudgeService judgeService;
    @SneakyThrows
    @RabbitListener(queues = {"judge_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("消息队列接收判题消息: {}", message);
        long questionSubmitId = Long.parseLong(message);
        try{
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        }catch(Exception e){
            // 错误直接丢弃, 避免消息无限循环
            channel.basicNack(deliveryTag, false, false);
        }

    }
}
