package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReviewDTO {
    @NotNull(message = "Mã thuê xe không được trống")
    private Integer carRentalId;

    @NotNull(message = "Đánh giá không được trống")
    @Min(value = 1, message = "Tối thiểu 1 sao")
    @Max(value = 5, message = "Tối đa 5 sao")
    private Integer reviewStar;

    @NotBlank(message = "Nhận xét không được trống")
    @Size(max = 500, message = "Nhận xét tối đa 500 ký tự")
    private String comment;
}
