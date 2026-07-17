package com.assignment.carrentingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RentalReportDTO {
    private Long carRentId;
    private String customerName;
    private String carName;
    private LocalDateTime pickupDate;
    private LocalDateTime returnDate;
    private BigDecimal rentPrice;
    private String status;
}
