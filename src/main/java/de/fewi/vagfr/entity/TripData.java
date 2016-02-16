package de.fewi.vagfr.entity;

import java.io.Serializable;

public class TripData implements Serializable {
    private String from;
    private String fromId;

    private String to;
    private String toId;

    private String product;
    private String number;

    private String departureTime;
    private String plannedDepartureTime;

    private long departureTimestamp;
    private long plannedDepartureTimestamp;

    private long departureDelay;
    private String position;

    public long getDepartureTimestamp() {
        return departureTimestamp;
    }

    public void setDepartureTimestamp(long departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

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

    public String getPlannedDepartureTime() {
        return plannedDepartureTime;
    }

    public void setPlannedDepartureTime(String plannedDepartureTime) {
        this.plannedDepartureTime = plannedDepartureTime;
    }

    public long getPlannedDepartureTimestamp() {
        return plannedDepartureTimestamp;
    }

    public void setPlannedDepartureTimestamp(long plannedDepartureTimestamp) {
        this.plannedDepartureTimestamp = plannedDepartureTimestamp;
    }

    public long getDepartureDelay() {
        return departureDelay;
    }

    public void setDepartureDelay(long departureDelay) {
        this.departureDelay = departureDelay;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
