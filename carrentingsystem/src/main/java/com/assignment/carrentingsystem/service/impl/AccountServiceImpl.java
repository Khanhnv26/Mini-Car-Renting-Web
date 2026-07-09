package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public List<Account> findAllCustomers() {
        return accountRepository.findByRole("Customer") ;
    }
}
