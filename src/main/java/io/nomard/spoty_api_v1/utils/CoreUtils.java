package io.nomard.spoty_api_v1.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CoreUtils {
    public static String referenceNumberGenerator(String prefix) {
        return prefix +
                "-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) +
                "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static class DateCalculations {

        public static LocalDateTime addSeconds(Integer seconds) {
            return LocalDateTime.now().plusSeconds(seconds);
        }

        public static LocalDateTime addMinutes(Integer minutes) {
            return LocalDateTime.now().plusMinutes(minutes);
        }

        public static LocalDateTime addHours(Integer hours) {
            return LocalDateTime.now().plusHours(hours);
        }

        public static LocalDateTime addDays(Integer days) {
            return LocalDateTime.now().plusDays(days);
        }

        public static LocalDateTime addWeeks(Integer weeks) {
            return LocalDateTime.now().plusWeeks(weeks);
        }

        public static LocalDateTime addMonths(Integer months) {
            return LocalDateTime.now().plusMonths(months);
        }

        public static LocalDateTime addYears(Integer years) {
            return LocalDateTime.now().plusYears(years);
        }
    }
}