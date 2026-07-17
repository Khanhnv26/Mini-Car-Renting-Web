package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

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
        return accountRepository.findByRole("Customer");
    }

    @Override
    public List<Account> findAvailableCustomerAccounts(Long keepAccountId) {
        return accountRepository.findByRole("Customer").stream()
                .filter(a -> a.getAccountId().equals(keepAccountId)
                        || !customerRepository.existsByAccount_AccountId(a.getAccountId()))
                .collect(Collectors.toList());
    }
}
