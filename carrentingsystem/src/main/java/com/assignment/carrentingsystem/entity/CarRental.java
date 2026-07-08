package com.assignment.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "CarRental")
public class CarRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarRentID")
    private Long carRentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID",nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarID",nullable = false)
    private Car car;

    @Column(name = "PickupDate", nullable = false)
    private LocalDateTime pickUpDate;

    @Column(name = "ReturnDate", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "RentPrice", nullable = false, precision = 18, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;


}

