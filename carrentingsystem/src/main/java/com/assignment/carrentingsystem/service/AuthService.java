package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService extends UserDetailsService {
    void register(RegisterDTO user);
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
