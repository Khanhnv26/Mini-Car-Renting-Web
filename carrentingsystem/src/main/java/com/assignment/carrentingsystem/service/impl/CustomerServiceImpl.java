package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

private final CustomerRepository customerRepository;
private final AccountRepository accountRepository;
private final CarRentalRepository carRentalRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer save(CustomerDTO customerDTO) {
        Account account = accountRepository.findById(customerDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        Customer customer;

        if(customerDTO.getCustomerId() != null){
            customer = customerRepository.findById(customerDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        } else {
            customer = new Customer();
        }
        customer.setFullName(customerDTO.getFullName());
        customer.setMobile(customerDTO.getMobile());
        customer.setBirthday(customerDTO.getBirthDate());
        customer.setIdentityCard(customerDTO.getIdentityCard());
        customer.setLicenceNumber(customerDTO.getLicenceNumber());
        customer.setLicenceDate(customerDTO.getLicenceDate());
        customer.setAccount(account);
        return customerRepository.save(customer);
    }

    @Override
    public Customer saveDirect(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deteleById(Long id) {
        if(carRentalRepository.existsByCustomer_CustomerId(id)){
            throw new RuntimeException("Không thể xoá khách hàng đang có giao dịch thuê");
        }
        customerRepository.deleteById(id);

    }

    @Override
    public Customer findByAccountId(Long accountId) {
        return customerRepository.findByAccountId(accountId);
    }
}
