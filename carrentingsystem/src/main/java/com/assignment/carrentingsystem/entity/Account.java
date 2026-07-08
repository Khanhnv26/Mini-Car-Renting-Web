package com.assignment.carrentingsystem.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Long accountId;

    @Column(name = "AccountName", nullable = false, length = 255)
    private String accountName;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "Password",nullable = false, length = 255)
    private String password;

    @Column(name = "Role", nullable = false, length = 20)
    private String role;

    @OneToOne(mappedBy = "account")
    private Customer customer;

}
