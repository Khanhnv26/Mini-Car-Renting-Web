package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarDTO {

    private Integer carId;

    @NotBlank(message = "Tên xe không được để trống")
    @Size(max = 200, message = "Tên xe tối đa 200 ký tự")
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

    @NotNull(message = "Ngày nhập kho không được để trống")
    @PastOrPresent(message = "Ngày nhập kho không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate importDate;

    @NotNull(message = "Hãng xe không được để trống")
    private Integer producerId;

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Giá thuê phải lớn hơn 0")
    private BigDecimal rentPrice;

    @NotBlank(message = "Trạng thái không được trống")
    @Size(max = 10, message = "Trạng thái tối đa 10 ký tự")
    private String status;

    @AssertTrue(message = "Ngày nhập kho không được trước năm sản xuất")
    public boolean isImportOnOrAfterModelYear() {
        if (importDate == null || carModelYear == null) {
            return true;
        }
        return importDate.getYear() >= carModelYear;
    }
}
