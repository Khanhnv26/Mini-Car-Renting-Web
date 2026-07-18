package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
    @Size(max = 200)
    private String email;

    @NotBlank(message = "Tên đăng nhập không được trống")
    @Size(max = 100, message = "Tên tài khoản tối đa 100 kí tự")
    private String accountName;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 200, message = "Mật khẩu từ 6 đến 200 kí tự")
    private String password;

    @NotBlank(message = "Họ tên không trống")
    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "SĐT không được để trống")
    @Size(max = 15, message = "SĐT tối đa 15 kí tự")
    @Pattern(regexp = "^0\\d{9,10}$", message = "SĐT phải gồm 10–11 chữ số và bắt đầu bằng 0")
    private String mobile;

    @NotNull(message = "Ngày sinh không được trống")
    @PastOrPresent(message = "Ngày sinh không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "Số CCCD không để trống")
    @Size(max = 20)
    private String identityCard;

    @NotBlank(message = "Số bằng lái không được để trống")
    @Size(max = 20)
    private String licenceNumber;

    @NotNull(message = "Ngày cấp bằng lái không được để trống")
    @PastOrPresent(message = "Ngày cấp bằng không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate licenceDate;

    @AssertTrue(message = "Khách hàng phải đủ 18 tuổi")
    public boolean isAdult() {
        return birthday == null || !birthday.isAfter(LocalDate.now().minusYears(18));
    }

    @AssertTrue(message = "Ngày cấp bằng phải từ khi khách hàng đủ 18 tuổi")
    public boolean isLicenceOnOrAfterAdultAge() {
        return birthday == null || licenceDate == null || !licenceDate.isBefore(birthday.plusYears(18));
    }
}
