package com.example.web_bansach.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time operations
 */
public class DateUtils {

    private DateUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    public static boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    public static boolean isDateInFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    public static boolean isDateTimeInPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    public static boolean isDateTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
}
