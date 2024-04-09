package com.petroineos.challenge.utils;

/**
 * @Author Dean Zhu
 * @Date 2024-04-09
 * @Version 1.0
 */
public class PetroPeriodUtil {
    public static final int MIN_PERIOD = 1;
    public static final int MAX_PERIOD = 24;

    /**
     * transform period to local time.
     * @param period
     * @return
     */
    public static String getTimeByPeriod(int period) {
        int hour = (22 + period) % 24;
        return String.format("%d:00", hour);
    }

    public static void main(String[] args) {
        System.out.println(getTimeByPeriod(1));
        System.out.println(getTimeByPeriod(10));
        System.out.println(getTimeByPeriod(24));
    }
}
