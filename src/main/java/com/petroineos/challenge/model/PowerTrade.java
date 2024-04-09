package com.petroineos.challenge.model;

import java.util.List;

/**
 * @Author Dean Zhu
 * @Date 2024-04-08
 * @Version 1.0
 */
public class PowerTrade {
    //Date of the power trade data. MM/DD/YYYY
    private String date;
    //Position name of this power trade record
    private String position;
    //Power trade data list in this date from the position.
    List<PeriodVolume> trades;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<PeriodVolume> getTrades() {
        return trades;
    }

    public void setTrades(List<PeriodVolume> trades) {
        this.trades = trades;
    }
}
