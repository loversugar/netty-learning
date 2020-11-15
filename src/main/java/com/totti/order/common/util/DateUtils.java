package com.totti.order.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String STANDARD_DATE_FORMAT_UTC_SHORT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String STANDARD_DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * 日期
     */
    public static final String DATE_STANDARD_FORMAT = "yyyy-MM-dd";

    public static final String DATE_COMPACT_FORMAT = "yyyyMMdd";
    /**
     * 时间格式
     */
    public static final String TIME_STANDARD_FORMAT = "HH:mm:ss";

    // ------------
    private static final DateTimeFormatter STD_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_FORMAT);
    private static final DateTimeFormatter STD_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_STANDARD_FORMAT);
    private static final DateTimeFormatter STD_TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_STANDARD_FORMAT);
    // ------------

    /** 格式化日期&时间 **/
    /** java.util.Date **/
    public static String format(Date date) {
        return format(dateToLocalDateTime(date), STANDARD_FORMAT);
    }

    public static String format(Date date, String format) {
        return format(dateToLocalDateTime(date), format);
    }

    /** DateTime **/
    public static String format(LocalDateTime date) {
        return date.format(STD_DATETIME_FORMATTER);
    }

    public static String format(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    // --------------
    public static String formatDate(Date date) {
        return formatDate(dateToLocalDateTime(date));
    }

    public static String formatDate(Date date, String format) {
        return formatDate(dateToLocalDateTime(date), format);
    }

    public static String formatDate(LocalDateTime date) {
        return date.format(STD_DATE_FORMATTER);
    }

    public static String formatDate(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    // -----------------
    public static String formatTime(Date date) {
        return formatTime(dateToLocalDateTime(date));
    }

    public static String formatTime(Date date, String format) {
        return formatTime(dateToLocalDateTime(date), format);
    }

    public static String formatTime(LocalDateTime date) {
        return date.format(STD_TIME_FORMATTER);
    }

    public static String formatTime(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    // -----------------

    /** 解析日期&时间 **/
    public static LocalDateTime parseDateTime(String date) {
        return LocalDateTime.parse(date, STD_DATETIME_FORMATTER);
    }

    public static LocalDate parseLocalDate(String date) {
        return LocalDate.parse(date, STD_DATE_FORMATTER);
    }

    public static Date parseDate(String date) {
        return localDateTimeToDate(LocalDateTime.parse(date, STD_DATETIME_FORMATTER));
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ----------

    public static Date getDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return localDateTimeToDate(LocalDateTime.of(date, time));
    }

    public static LocalDateTime getDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return LocalDateTime.of(date, time);
    }

    /** 计算开始/截止日期 **/
    /** day **/
    public static LocalDate getStartOfDay(LocalDateTime dateTime) {
        return LocalDate.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }

    public static LocalDate getEndOfDay(LocalDateTime dateTime) {
        return LocalDate.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }

    /** week **/
    public static LocalDate getStartOfWeek(LocalDate date) {
        return getStartOfWeek(date, Locale.FRANCE);
    }

    public static LocalDate getStartOfWeek(LocalDate date, Locale locale) {
        return date.with(WeekFields.of(locale).dayOfWeek(), 1L);
    }

    public static LocalDate getEndOfWeek(LocalDate date) {
        return getEndOfWeek(date, Locale.FRANCE);
    }

    public static LocalDate getEndOfWeek(LocalDate date, Locale locale) {
        return date.with(WeekFields.of(locale).dayOfWeek(), 7L);
    }

    /** month **/
    public static LocalDate getStartOfMonth(LocalDate date) {
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay;
    }

    public static LocalDate getEndOfMonth(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay;
    }

    /** year **/
    public static LocalDate getStartOfYear(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.firstDayOfYear());
        return lastDay;
    }

    public static LocalDate getEndOfYear(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfYear());
        return lastDay;
    }

    // --------
    /** years **/
    public static int yearsBetween(LocalDate start, LocalDate end) {
        return Period.between(start, end).getYears();
    }

    /** months **/
    public static int monthsBetween(LocalDate start, LocalDate end) {
        return Period.between(start, end).getMonths();
    }

    /** days **/
    public static int daysBetween(LocalDate start, LocalDate end) {
        return Period.between(start, end).getDays();
    }

    /** hours **/
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toHours();
    }

    /** minutes **/
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }

    /** seconds **/
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).getSeconds();
    }

    // --------
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ---------
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
