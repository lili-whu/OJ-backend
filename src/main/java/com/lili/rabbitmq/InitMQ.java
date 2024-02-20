package com.lili.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
@Slf4j
public class InitMQ{
    public static void doInitMQ() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            // 创建connection
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "judge_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            // 创建队列

            String queueName = "judge_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routing_key");
            log.info("消息队列启动成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args){
        doInitMQ();
    }
}
