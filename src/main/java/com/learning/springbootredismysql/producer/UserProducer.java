package com.learning.springbootredismysql.producer;

import com.learning.springbootredismysql.config.RabbitConfig;
import com.learning.springbootredismysql.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(User user) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.QUEUE,
                user);

        System.out.println("Published : " + user.getName());
    }
}