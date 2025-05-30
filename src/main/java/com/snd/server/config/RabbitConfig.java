package com.snd.server.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.snd.server.constant.RabbitMqConstant;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange otpExchange() {
        return new TopicExchange(RabbitMqConstant.OTP_EXCHANGE);
    }

    @Bean
    public Queue otpQueue() {
        return new Queue(RabbitMqConstant.OTP_QUEUE, true);
    }

    @Bean
    public Binding otpBinding(Queue otpQueue, TopicExchange otpExchange) {
        return BindingBuilder.bind(otpQueue).to(otpExchange).with(RabbitMqConstant.OTP_ROUTING_KEY);
    }

    // Mono
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}