package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RentalRequest {
    @NotEmpty(message = "Phải chọn ít nhất 1 xe")
    private List<Integer> carIds;

    @NotNull(message = "Ngày nhận xe không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pickupDate;

    @NotNull(message = "Ngày trả xe không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate returnDate;

    @AssertTrue(message = "Ngày nhận xe phải trước ngày trả xe")
    public boolean isValidDateRange() {
        return pickupDate == null || returnDate == null || pickupDate.isBefore(returnDate);
    }

    @AssertTrue(message = "Ngày nhận xe không được ở quá khứ")
    public boolean isPickupNotInPast() {
        return pickupDate == null || !pickupDate.isBefore(LocalDate.now());
    }

    @AssertTrue(message = "Ngày trả xe không được ở quá khứ")
    public boolean isReturnNotInPast() {
        return returnDate == null || !returnDate.isBefore(LocalDate.now());
    }
}
