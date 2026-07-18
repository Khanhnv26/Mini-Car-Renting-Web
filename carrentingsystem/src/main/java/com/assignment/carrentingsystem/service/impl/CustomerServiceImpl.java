package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.AccountRepository;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.CustomerService;
import com.assignment.carrentingsystem.util.CustomerDateRules;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CarRentalRepository carRentalRepository;


    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(c.getCustomerId());
        dto.setFullName(c.getFullName());
        dto.setMobile(c.getMobile());
        dto.setBirthday(c.getBirthday());
        dto.setIdentityCard(c.getIdentityCard());
        dto.setLicenceNumber(c.getLicenceNumber());
        dto.setLicenceDate(c.getLicenceDate());
        if (c.getAccount() != null) {
            dto.setAccountId(c.getAccount().getAccountId());
            dto.setAccountName(c.getAccount().getAccountName());
            dto.setAccountEmail(c.getAccount().getEmail());
        }
        return dto;
    }

    private Customer toEntity(CustomerDTO dto, Customer customer, boolean lockAccount) {
        customer.setFullName(dto.getFullName());
        customer.setMobile(dto.getMobile());
        customer.setBirthday(dto.getBirthday());
        customer.setIdentityCard(dto.getIdentityCard());
        customer.setLicenceNumber(dto.getLicenceNumber());
        customer.setLicenceDate(dto.getLicenceDate());
        if (!lockAccount) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            customer.setAccount(account);
        }
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }
    @Override
    public CustomerDTO findDTOById(Integer id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        return toDTO(c);
    }
    @Override
    @Transactional
    public Customer save(CustomerDTO customerDTO) {
        Customer customer;
        boolean lockAccount;
        if (customerDTO.getCustomerId() != null) {
            customer = customerRepository.findById(customerDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            lockAccount = true;
            if (customer.getAccount() != null) {
                customerDTO.setAccountId(customer.getAccount().getAccountId());
            }
        } else {
            if (customerDTO.getAccountId() == null) {
                throw new RuntimeException("Tài khoản không được trống");
            }
            if (customerRepository.existsByAccount_AccountId(customerDTO.getAccountId())) {
                throw new RuntimeException("Tài khoản này đã có hồ sơ khách hàng");
            }
            customer = new Customer();
            lockAccount = false;
        }
        CustomerDateRules.validate(customerDTO.getBirthday(), customerDTO.getLicenceDate());
        return customerRepository.save(toEntity(customerDTO, customer, lockAccount));
    }

    @Override
    public Customer findByEmail(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Tài khoản không tồn tại với email: " + email);
        }
        return customerRepository.findByAccountId(account.getAccountId());
    }

    @Override
    @Transactional
    public void updateProfile(String email, Customer updatedData) {
        CustomerDateRules.validate(updatedData.getBirthday(), updatedData.getLicenceDate());
        Customer current = findByEmail(email);
        current.setFullName(updatedData.getFullName());
        current.setMobile(updatedData.getMobile());
        current.setBirthday(updatedData.getBirthday());
        current.setIdentityCard(updatedData.getIdentityCard());
        current.setLicenceNumber(updatedData.getLicenceNumber());
        current.setLicenceDate(updatedData.getLicenceDate());
        customerRepository.save(current);
    }
    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (carRentalRepository.existsByCustomer_CustomerId(id)) {
            throw new RuntimeException("Không thể xoá khách hàng đang có giao dịch thuê");
        }
        customerRepository.deleteById(id);
    }
    @Override
    public Customer findByAccountId(Integer accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    @Override
    public Page<Customer> findPaginated(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return customerRepository.findCustomersWithFilters(cleanKeyword, pageable);
    }
}
