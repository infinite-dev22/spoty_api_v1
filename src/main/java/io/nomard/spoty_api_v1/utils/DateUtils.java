package io.nomard.spoty_api_v1.utils;

import java.time.LocalDateTime;

public class DateUtils {

    public static LocalDateTime addSeconds(long seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    public static LocalDateTime addMinutes(long minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    public static LocalDateTime addHours(long hours) {
        return LocalDateTime.now().plusHours(hours);
    }

    public static LocalDateTime addDays(long days) {
        return LocalDateTime.now().plusDays(days);
    }

    public static LocalDateTime addWeeks(long weeks) {
        return LocalDateTime.now().plusWeeks(weeks);
    }

    public static LocalDateTime addMonths(long months) {
        return LocalDateTime.now().plusMonths(months);
    }

    public static LocalDateTime addYears(long years) {
        return LocalDateTime.now().plusYears(years);
    }
}