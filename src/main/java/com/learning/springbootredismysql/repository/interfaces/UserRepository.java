package com.learning.springbootredismysql.repository.interfaces;

import com.learning.springbootredismysql.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}