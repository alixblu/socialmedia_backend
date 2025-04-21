package com.example.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        return dateTime.format(formatter);
    }

    public static LocalDateTime parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        return LocalDateTime.parse(date, formatter);
    }
}