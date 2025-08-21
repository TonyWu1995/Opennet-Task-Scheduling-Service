package com.example.demo.mq;

import com.example.demo.config.RocketMQConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RocketMQProducer {

    private final RocketMQTemplate rocketMQTemplate;

    private final RocketMQConfiguration config;

    public RocketMQProducer(final RocketMQTemplate rocketMQTemplate, final RocketMQConfiguration config) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.config = config;
    }

    public boolean send(Message message) {
        try {
            SendResult result = rocketMQTemplate.syncSend(config.getTopic(), message);
            return result.getSendStatus() == SendStatus.SEND_OK;
        } catch (Exception e) {
            log.error("Failed to send message: {}", message, e);
            return false;
        }
    }

}
