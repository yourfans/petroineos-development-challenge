package com.petroineos.challenge.model;

/**
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
public class PeriodVolume {
    //The period number starts at 1,
    //which is the first period of the day and starts at 23:00 (11 pm) on the previous day.
    //values: [1..24]
    private int period;
    //data of the period corresponding to the date.
    private int volume;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
