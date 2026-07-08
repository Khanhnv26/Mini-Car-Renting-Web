package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer findById(Long id);
    Customer save(CustomerDTO customer);
    Customer saveDirect(Customer customer);
    void deteleById(Long id);
    Customer findByAccountId(Long accountId);

}
