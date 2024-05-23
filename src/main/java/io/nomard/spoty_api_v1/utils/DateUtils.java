package io.nomard.spoty_api_v1.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date addSeconds(int seconds) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.SECOND, seconds); // Add 1 month
        return calendar.getTime();
    }

    public static Date addMinutes(int minutes) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.MINUTE, minutes); // Add 1 month
        return calendar.getTime();
    }

    public static Date addHours(int hours) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.HOUR, hours); // Add 1 month
        return calendar.getTime();
    }

    public static Date addDays(int days) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, days); // Add 1 month
        return calendar.getTime();
    }

    public static Date addWeeks(int weeks) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks); // Add 1 month
        return calendar.getTime();
    }

    public static Date addMonths(int months) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, months); // Add 1 month
        return calendar.getTime();
    }

    public static Date addYears(int years) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.YEAR, years); // Add 1 month
        return calendar.getTime();
    }
}