package com.example.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        // BASE64-ключ для подписи JWT
        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="
        );

        // Токен действует 24 часа
        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                86400000L
        );

        userDetails = User.builder()
                .username("test@test.com")
                .password("password")
                .roles("USER")
                .build();
    }


    // Проверяем, что токен вообще создаётся
    @Test
    void generateToken_shouldCreateToken() {

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }


    // Проверяем, что из токена достаётся правильный email
    @Test
    void extractUsername_shouldReturnCorrectEmail() {

        String token = jwtService.generateToken(userDetails);

        String username =
                jwtService.extractUsername(token);

        assertThat(username)
                .isEqualTo("test@test.com");
    }


    // Проверяем валидный токен
    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {

        String token = jwtService.generateToken(userDetails);

        boolean result =
                jwtService.isTokenValid(token, userDetails);

        assertThat(result).isTrue();
    }


    // Проверяем, что токен другого пользователя не подходит
    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {

        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = User.builder()
                .username("another@test.com")
                .password("password")
                .roles("USER")
                .build();

        boolean result =
                jwtService.isTokenValid(token, anotherUser);

        assertThat(result).isFalse();
    }
}