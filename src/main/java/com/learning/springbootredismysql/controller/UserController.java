package com.learning.springbootredismysql.controller;

import com.learning.springbootredismysql.entity.User;
import com.learning.springbootredismysql.producer.UserProducer;
import com.learning.springbootredismysql.repository.interfaces.UserRepository;
import com.learning.springbootredismysql.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    private final UserProducer producer;

    public UserController(UserService service,  UserProducer producer) {
        this.service = service;
        this.producer = producer;
    }

    @PostMapping
    public User save(@RequestBody User user) {
        return service.save(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted";
    }

    //MQ API
    @PostMapping("/mq")
    public String create(@RequestBody User user) {

        producer.publish(user);

        return "Message Published to RabbitMQ with user : " + user.getName();
    }
}