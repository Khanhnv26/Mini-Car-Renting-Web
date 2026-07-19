package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.config.AppConfig;
import com.assignment.carrentingsystem.dto.RegisterForm;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.AuthService;
import com.assignment.carrentingsystem.util.CustomerDateRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void register(RegisterForm user) {
        CustomerDateRules.validate(user.getBirthday(), user.getLicenceDate());
        if (accountRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (accountRepository.existsByAccountName(user.getAccountName())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        Account account = new Account();
        account.setAccountName(user.getAccountName());
        account.setPassword(AppConfig.hashPassword(user.getPassword()));
        account.setEmail(user.getEmail());
        account.setRole("Customer");

        Account newAccount = accountRepository.save(account);

        Customer customer = new Customer();
        customer.setFullName(user.getFullName());
        customer.setMobile(user.getMobile());
        customer.setBirthday(user.getBirthday());
        customer.setIdentityCard(user.getIdentityCard());
        customer.setLicenceNumber(user.getLicenceNumber());
        customer.setLicenceDate(user.getLicenceDate());
        customer.setAccount(newAccount);
        customerRepository.save(customer);
    }

    @Override
    public Account login(String accountName, String password) {
        Account account = accountRepository.findByAccountName(accountName);
        if (account == null || !AppConfig.matches(password, account.getPassword())) {
            throw new RuntimeException("Sai tên đăng nhập hoặc mật khẩu");
        }
        return account;
    }
}
