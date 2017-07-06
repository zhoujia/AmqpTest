package com.zj.test;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhoujia on 2017/7/5.
 *
 */
@Configuration
public class RabbitConfig {
    static final String FIBO_CALCULATOR_EXCHANGE_NAME = "app.Exchange";
    private static final String REQUEST_QUEUE_NAME = "app.request";
    private static final String REQUEST_QUEUE_NAME1 = "app.request1";
    private static final String REPLY_QUEUE_NAME = "app.reply";
    static final String ROUTING_KEY_NAME1 = "route1";
    static final String ROUTING_KEY_NAME2 = "route2";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(FIBO_CALCULATOR_EXCHANGE_NAME);
    }

    @Bean
    Queue requestQueue() {
        return QueueBuilder.durable(REQUEST_QUEUE_NAME).build();
    }

    @Bean
    Queue requestQueue1() {
        return QueueBuilder.durable(REQUEST_QUEUE_NAME1).build();
    }

    @Bean
    Queue replyQueue() {
        return QueueBuilder.durable(REPLY_QUEUE_NAME).build();
    }

    @Bean
    Binding binding() {

        return BindingBuilder.bind(requestQueue()).to(exchange()).with(ROUTING_KEY_NAME1);
    }

    @Bean
    Binding binding1() {
        return BindingBuilder.bind(requestQueue1()).to(exchange()).with(ROUTING_KEY_NAME2);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    AsyncRabbitTemplate template() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(REPLY_QUEUE_NAME);
        rabbitTemplate.setExchange(FIBO_CALCULATOR_EXCHANGE_NAME);
        return new AsyncRabbitTemplate(rabbitTemplate, container);
    }



    // 在spring容器中添加一个监听类
    @Bean
    Receiver receiver() {
        return new Receiver();
    }
}
