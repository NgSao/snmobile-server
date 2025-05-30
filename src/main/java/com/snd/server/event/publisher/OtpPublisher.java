package com.snd.server.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.snd.server.constant.RabbitMqConstant;
import com.snd.server.event.domain.OtpEvent;

@Component
public class OtpPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OtpPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOtp(OtpEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConstant.OTP_EXCHANGE, RabbitMqConstant.OTP_ROUTING_KEY, event);
    }
}
