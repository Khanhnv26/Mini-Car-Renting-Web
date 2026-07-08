package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.RegisterDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO user) {
        if(accountRepository.existsByEmail(user.getEmail())) {
            throw new UsernameNotFoundException("Email đã tồn tại");

        }

        Account account = new Account();
        account.setAccountName(user.getAccountName());
        account.setPassword(passwordEncoder.encode(user.getPassword()));
        account.setEmail(user.getEmail());
        account.setRole("Customer");

        Account newAccount = accountRepository.save(account);

        Customer customer = new Customer();
        customer.setFullName(user.getFullName());
        customer.setMobile(user.getMobile());
        customer.setBirthday(user.getBirthDate());
        customer.setIdentityCard(user.getIdentityCard());
        customer.setLicenceNumber(user.getLicenceNumber());
        customer.setLicenceDate(user.getLicenceDate());
        customer.setAccount(newAccount);
        customerRepository.save(customer);
    }
}
