package de.fewi.ptwa.entity;

import java.io.Serializable;

public class DepartureData implements Serializable, Comparable<DepartureData> {
    private String to;
    private String toId;

    private String product;
    private String number;

    private String departureTime;

    private long departureTimestamp;
    private long departureDelay;

    private int departureTimeInMinutes;

    private String platform;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public long getDepartureTimestamp() {
        return departureTimestamp;
    }

    public void setDepartureTimestamp(long departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public long getDepartureDelay() {
        return departureDelay;
    }

    public void setDepartureDelay(long departureDelay) {
        this.departureDelay = departureDelay;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getDepartureTimeInMinutes() {
        return departureTimeInMinutes;
    }

    public void setDepartureTimeInMinutes(int departureTimeInMinutes) {
        this.departureTimeInMinutes = departureTimeInMinutes;
    }

    @Override
    public int compareTo(DepartureData o) {
        return this.departureTimeInMinutes-o.departureTimeInMinutes;
    }


}
