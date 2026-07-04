package com.learning.springbootredismysql.consumer;

import com.learning.springbootredismysql.config.RabbitConfig;
import com.learning.springbootredismysql.entity.User;
import com.learning.springbootredismysql.repository.interfaces.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {

    private final UserRepository repository;

    public UserConsumer(UserRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(User user) {

        System.out.println("Received : " + user.getName());

        repository.save(user);

        System.out.println("Saved into MySQL");
    }

}