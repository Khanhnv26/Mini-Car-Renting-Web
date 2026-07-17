package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.Email;
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
public class RegisterForm {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Tên đăng nhập không được trống")
    @Size(max = 255, message = "Tên tài khoản tối đa 255 kí tự")
    private String accountName;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu tối thiểu 6 kí tự")
    private String password;

    @NotBlank(message = "Họ tên không trống")
    @Size(max = 255)
    private String fullName;

    @NotBlank(message = "SĐT không để trống")
    private String mobile;

    @NotNull(message = "Ngày sinh không được trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "Số CCCD không để trống")
    @Size(max = 20)
    private String identityCard;

    @NotBlank(message = "Số bằng lái không được để trống")
    @Size(max = 20)
    private String licenceNumber;

    @NotNull(message = "Ngày cấp bằng lái không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate licenceDate;

}
