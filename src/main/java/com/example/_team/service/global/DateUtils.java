package com.example._team.service.global;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String formatDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.format(FORMATTER) + " - " + endDate.format(FORMATTER);
    }
}