package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomerDTO {
    private Long customerId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
    private String fullName;

    @NotBlank(message = "Số ĐT không được trống")
    @Size(max = 20, message = "SĐT tối đa 20 kí tự")
    private String mobile;

    @NotNull(message = "Ngày sinh không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotBlank(message = "Số CCCD không được để trống")
    @Size(max = 20, message = "Số CCCD tối đa 20 ký tự")
    private String identityCard;

    @NotBlank(message = "Số bằng lái không được để trống")
    @Size(max = 20, message = "Số bằng lái tối đa 20 ký tự")
    private String licenceNumber;

    @NotNull(message = "Ngày cấp bằng không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate licenceDate;

    @NotNull(message = "Tài khoản không được trống")
    private Long accountId;
}
