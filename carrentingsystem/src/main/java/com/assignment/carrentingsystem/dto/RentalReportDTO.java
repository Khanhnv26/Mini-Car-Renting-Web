package com.assignment.carrentingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentalReportDTO {
    private Integer carRenId;
    private String customerName;
    private String carName;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private BigDecimal rentPrice;
    private String status;
}
