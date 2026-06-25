package com.learning.springbootredismysql.service;

import com.learning.springbootredismysql.entity.User;
import com.learning.springbootredismysql.repository.interfaces.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;

    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository repository,
                       RedisTemplate<String, Object> redisTemplate) {

        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public User save(User user) {
        User saved = repository.save(user);
        redisTemplate.opsForValue().set("user:" + saved.getId(), saved);
        return saved;
    }

    public User getById(Long id) {

        long start = System.nanoTime();

        String key = "user:" + id;

        log.info("Looking for key : {}", key);

        User cached = (User) redisTemplate.opsForValue().get(key);

        if (cached != null) {

            long end = System.nanoTime();

            log.info("✅ Cache HIT");
            log.info("Retrieved from Redis");
            double executionTime = (end - start) / 1_000_000.0;
            log.info("Source={}, Key={}, ExecutionTime={} ms",
                    "REDIS",
                    key,
                    String.format("%.3f", executionTime));

            return cached;
        }

        log.info("❌ Cache MISS");
        log.info("Fetching from MySQL...");

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        redisTemplate.opsForValue().set(key, user);
        log.info("User Saved to Redis {}", key);

        long end = System.nanoTime();

        double executionTime = (end - start) / 1_000_000.0;
        log.info("Source={}, Key={}, ExecutionTime={} ms",
                "MYSQL",
                key,
                String.format("%.3f", executionTime));

        return user;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
        // Intentionally commented to not clear cache, and to demonstrate importance of invalidating an entry if got deleted from DB
        // redisTemplate.delete("user:" + id);
    }
}