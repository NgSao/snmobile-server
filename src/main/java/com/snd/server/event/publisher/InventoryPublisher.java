package com.snd.server.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.snd.server.constant.RabbitMqConstant;
import com.snd.server.event.domain.InventoryEvent;

@Component
public class InventoryPublisher {

    private final RabbitTemplate rabbitTemplate;

    public InventoryPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendInventory(InventoryEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConstant.OTP_EXCHANGE, RabbitMqConstant.OTP_ROUTING_KEY, event);
    }
}
