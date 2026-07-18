package com.assignment.carrentingsystem.util;

import java.time.LocalDate;

public final class CustomerDateRules {

    private CustomerDateRules() {
    }

    public static void validate(LocalDate birthday, LocalDate licenceDate) {
        LocalDate today = LocalDate.now();
        if (birthday == null) {
            throw new RuntimeException("Ngày sinh không được để trống");
        }
        if (licenceDate == null) {
            throw new RuntimeException("Ngày cấp bằng không được để trống");
        }
        if (birthday.isAfter(today)) {
            throw new RuntimeException("Ngày sinh không được ở tương lai");
        }
        if (birthday.isAfter(today.minusYears(18))) {
            throw new RuntimeException("Khách hàng phải đủ 18 tuổi");
        }
        if (licenceDate.isAfter(today)) {
            throw new RuntimeException("Ngày cấp bằng không được ở tương lai");
        }
        if (licenceDate.isBefore(birthday.plusYears(18))) {
            throw new RuntimeException("Ngày cấp bằng phải từ khi khách hàng đủ 18 tuổi");
        }
    }
}
