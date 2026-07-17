package com.assignment.carrentingsystem.config;

import com.assignment.carrentingsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthService authService;
    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/", "/cars", "/reviews", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("Admin")
                .requestMatchers("/customer/**").hasRole("Customer")
                .anyRequest().authenticated())
                .formLogin(f -> f
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                .csrf(c -> c.disable())
                .userDetailsService(authService);
        return http.build();
    }
}
