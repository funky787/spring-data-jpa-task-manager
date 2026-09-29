package com.example.taskmanager.config;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {


            String adminEmail = "admin@test.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                User admin = new User(
                        null,
                        adminEmail,
                        passwordEncoder.encode("admin123"),
                        Role.ADMIN
                );

                userRepository.save(admin);

                System.out.println("ADMIN создан: " + adminEmail);
            }



            String userEmail = "user@test.com";

            if (userRepository.findByEmail(userEmail).isEmpty()) {

                User user = new User(
                        null,
                        userEmail,
                        passwordEncoder.encode("user123"),
                        Role.USER
                );

                userRepository.save(user);

                System.out.println("USER создан: " + userEmail);
            }
        };
    }
}