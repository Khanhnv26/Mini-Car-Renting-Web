package com.assignment.carrentingsystem.service;


import com.assignment.carrentingsystem.entity.Account;

import java.util.List;

public interface AccountService {
    Account findByEmail(String email);
    Account save(Account account);
    boolean existsByEmail(String email);
    List<Account> findAllCustomers();
}
