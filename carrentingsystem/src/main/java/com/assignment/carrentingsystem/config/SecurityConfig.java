package com.assignment.carrentingsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/", "/register", "/login", "/css/**","/js/**","/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("Admin")
                .requestMatchers("/customer/**").hasRole("Customer")
                .anyRequest().authenticated())
                .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll())
                .csrf(c -> c.disable());
        return http.build();

    }

}
