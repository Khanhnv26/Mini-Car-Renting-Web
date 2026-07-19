package com.assignment.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "Car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarID")
    private Integer carId;

    @Column(name = "CarName", length = 200, nullable = false)
    private String carName;

    @Column(name = "CarModelYear", nullable = false)
    private Integer carModelYear;

    @Column(name = "Color", length = 50, nullable = false)
    private String color;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Column(name = "Description", nullable = false, length = 1000)
    private String description;

    @Column(name = "ImportDate", nullable = false)
    private LocalDate importDate;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;

    @Column(name = "RentPrice", nullable = false, precision = 10, scale = 0)
    private BigDecimal rentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProducerID", nullable = false)
    private CarProducer carProducer;
}
