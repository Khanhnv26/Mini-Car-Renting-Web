package com.assignment.carrentingsystem.util;

import java.math.BigDecimal;

public final class PriceRangeRules {

    private PriceRangeRules() {
    }

    public static String validateOptionalRange(BigDecimal min, BigDecimal max) {
        if (min != null && min.signum() < 0) {
            return "Giá lọc không được âm";
        }
        if (max != null && max.signum() < 0) {
            return "Giá lọc không được âm";
        }
        if (min != null && max != null && min.compareTo(max) > 0) {
            return "Giá từ không được lớn hơn giá đến";
        }
        return null;
    }
}
