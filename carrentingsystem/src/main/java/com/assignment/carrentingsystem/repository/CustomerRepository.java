package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("from Customer c where c.account.accountId =:accountId")
    Customer findByAccountId(@Param("accountId") Long accountId);

}
