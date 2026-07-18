package com.assignment.carrentingsystem.util;

import java.time.LocalDate;

public final class DateRangeRules {

    private DateRangeRules() {
    }

    public static String validateOptionalRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            return "Từ ngày không được sau Đến ngày";
        }
        return null;
    }
}
