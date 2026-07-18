package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.AssertTrue;
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
public class ProfileForm {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 200, message = "Họ tên tối đa 200 ký tự")
    private String fullName;

    @NotBlank(message = "SĐT không được để trống")
    @Size(max = 15, message = "SĐT tối đa 15 kí tự")
    @Pattern(regexp = "^0\\d{9,10}$", message = "SĐT phải gồm 10–11 chữ số và bắt đầu bằng 0")
    private String mobile;

    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "Số CCCD không được để trống")
    @Size(max = 20, message = "Số CCCD tối đa 20 ký tự")
    private String identityCard;

    @NotBlank(message = "Số bằng lái không được để trống")
    @Size(max = 20, message = "Số bằng lái tối đa 20 ký tự")
    private String licenceNumber;

    @NotNull(message = "Ngày cấp bằng không được để trống")
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
