package com.spring.blog.repository;

import java.util.Optional;

import com.spring.blog.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);

    default Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
}
