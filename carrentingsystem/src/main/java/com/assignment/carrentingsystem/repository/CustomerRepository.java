package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("from Customer c where c.account.accountId =:accountId")
    Customer findByAccountId(@Param("accountId") Long accountId);

    boolean existsByAccount_AccountId(Long accountId);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:keyword IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.mobile) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> findCustomersWithFilters(@Param("keyword") String keyword, Pageable pageable);
}
