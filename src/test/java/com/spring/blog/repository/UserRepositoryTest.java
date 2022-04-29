package com.spring.blog.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.spring.blog.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        @Test
        public void givenUsername_whenFindByUsernameOrEmail_thenReturnUser() {

                // given
                String username = "testuser";
                userRepository.save(User.builder()
                                .username(username)
                                .email("email")
                                .password("password")
                                .firstName("firstName")
                                .lastName("lastName")
                                .build());

                // when
                Optional<User> user = userRepository.findByUsernameOrEmail(username);

                // then
                assertAll(
                                () -> assertTrue(user.isPresent()),
                                () -> assertEquals(username, user.get().getUsername()),
                                () -> assertEquals("email", user.get().getEmail()),
                                () -> assertEquals("password", user.get().getPassword()),
                                () -> assertEquals("firstName", user.get().getFirstName()),
                                () -> assertEquals("lastName", user.get().getLastName()));
        }

        @Test
        public void givenEmail_whenFindByUsernameOrEmail_thenReturnUser() {

                // given
                String email = "testuser@gmail.com";
                userRepository.save(User.builder()
                                .username("testuser")
                                .email(email)
                                .password("password")
                                .firstName("firstName")
                                .lastName("lastName")
                                .build());

                // when
                Optional<User> user = userRepository.findByUsernameOrEmail(email);

                // then
                assertAll(
                                () -> assertTrue(user.isPresent()),
                                () -> assertEquals(email, user.get().getEmail()),
                                () -> assertEquals("testuser", user.get().getUsername()),
                                () -> assertEquals("password", user.get().getPassword()),
                                () -> assertEquals("firstName", user.get().getFirstName()),
                                () -> assertEquals("lastName", user.get().getLastName()));
        }

}
