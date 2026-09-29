package com.example.taskmanager.config;

import com.example.taskmanager.security.CustomAccessDeniedHandler;
import com.example.taskmanager.security.CustomAuthEntryPoint;
import com.example.taskmanager.security.JwtAuthFilter;
import com.example.taskmanager.service.UserDetailsServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            JwtAuthFilter jwtAuthFilter,
            CustomAuthEntryPoint customAuthEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http


                .csrf(csrf -> csrf.disable())



                .authorizeHttpRequests(auth -> auth


                        .requestMatchers("/auth/**")
                        .permitAll()


                        .requestMatchers("/login")
                        .permitAll()


                        .requestMatchers("/css/**")
                        .permitAll()


                        .requestMatchers("/h2-console/**")
                        .permitAll()


                        .requestMatchers("/error")
                        .permitAll()


                        .anyRequest()
                        .authenticated()
                )



                .formLogin(form -> form


                        .loginPage("/login")


                        .loginProcessingUrl("/login")


                        .usernameParameter("username")


                        .passwordParameter("password")


                        .defaultSuccessUrl("/web/tasks", true)


                        .failureUrl("/login?error")

                        .permitAll()
                )



                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout")

                        .permitAll()
                )



                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(customAuthEntryPoint)

                        .accessDeniedHandler(customAccessDeniedHandler)
                )



                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                )



                .authenticationProvider(
                        authenticationProvider()
                )



                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}