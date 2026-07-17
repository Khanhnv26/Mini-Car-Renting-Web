package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Customer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer findById(Long id);
    CustomerDTO findDTOById(Long id);
    Customer save(CustomerDTO customer);
    void deleteById(Long id);
    Customer findByAccountId(Long accountId);
    Customer findByEmail(String email);
    void updateProfile(String email, Customer data);
    Page<Customer> findPaginated(String keyword, int page, int size, String sortBy, String sortDir);
}
