package com.assignment.carrentingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarRentalDTO {
    @NotNull(message = "Xe không được để trống")
    private Long carId;

    @NotNull(message = "Ngày nhận xe không được để trống")
    private LocalDateTime pickupDate;

    @NotNull(message = "Ngày trả xe không được để trống")
    private LocalDateTime returnDate;

    @AssertTrue(message = "Ngày nhận xe phải trước ngày trả xe")
    public boolean isValidDateRange() {
        return pickupDate != null && returnDate != null && pickupDate.isBefore(returnDate);
    }
}
