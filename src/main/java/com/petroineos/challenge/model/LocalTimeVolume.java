package com.petroineos.challenge.model;

/**
 * @Author Dean Zhu
 * @Date 2024-04-09
 * @Version 1.0
 */
public class LocalTimeVolume {
    private String localTime;
    private int volume;

    public LocalTimeVolume() {
    }

    public LocalTimeVolume(String localTime, int volume) {
        this.localTime = localTime;
        this.volume = volume;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
