package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarDTO {

    private Long carId;

    @NotBlank(message = "Tên xe không được để trống")
    @Size(max = 255, message = "Tên xe tối đa 255 ký tự")
    private String carName;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Min(value = 2000, message = "Năm sản xuất từ 2000 trở lên")
    private Integer carModelYear;

    @NotBlank(message = "Màu không được để trống")
    @Size(max = 50, message = "Màu tối đa 50 kí tự")
    private String color;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa tối thiểu là 1")
    private Integer capacity;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDate importDate;

    @NotNull(message = "Hãng xe không được để trống")
    private Long producerId;

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá thuê không âm")
    private BigDecimal rentPrice;

    @NotBlank(message = "Trạng thái không được trống")
    private String status;
}
