package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByEmail(String email);

    boolean existsByAccountName(String accountName);

    boolean existsByEmailAndAccountIdNot(String email, Integer accountId);

    boolean existsByAccountNameAndAccountIdNot(String accountName, Integer accountId);

    Account findByEmail(String email);

    Account findByAccountName(String accountName);

    List<Account> findByRole(String role);
}
