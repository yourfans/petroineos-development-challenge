package com.petroineos.challenge.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date related utils
 * @Author Dean Zhu
 * @Date 2024-04-09
 * @Version 1.0
 */
public class DateUtil {
    public static final String PATTERN_POWER_TRADE_DATA = "MM/dd/YYYY";
    public static final String PATTERN_AGGREGATION_CSV_FILE = "YYYYMMdd_HHmm";

    /**
     * Format datetime with specified time zone and pattern.
     * @param dateTime
     * @param zoneId
     * @param pattern
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime, String zoneId, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(zoneId));
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.format(dateTimeFormatter);
    }

    public static void main(String[] args) {
        System.out.println(formatDateTime(LocalDateTime.now(), "Europe/London", PATTERN_POWER_TRADE_DATA));
    }
}
