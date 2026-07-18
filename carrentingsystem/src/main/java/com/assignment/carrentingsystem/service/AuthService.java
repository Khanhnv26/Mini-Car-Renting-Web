package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.RegisterForm;
import com.assignment.carrentingsystem.entity.Account;

public interface AuthService {
    void register(RegisterForm user);

    Account login(String accountName, String password);
}
