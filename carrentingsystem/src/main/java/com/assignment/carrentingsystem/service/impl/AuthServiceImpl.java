package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.RegisterForm;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void register(RegisterForm user) {
        if (accountRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
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
        customer.setBirthday(user.getBirthday());
        customer.setIdentityCard(user.getIdentityCard());
        customer.setLicenceNumber(user.getLicenceNumber());
        customer.setLicenceDate(user.getLicenceDate());
        customer.setAccount(newAccount);
        customerRepository.save(customer);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if(account == null) {
            throw new UsernameNotFoundException("Email không tồn tại: " + email);
        }
        return User.withUsername(account.getEmail())
                   .password(account.getPassword())
                   .roles(account.getRole())
                   .build();
    }
}
