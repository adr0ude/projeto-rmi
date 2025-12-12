package com.rmi.commons.model;

public class AvgData {

    private String id;
    private String type;
    private double value;
    private long timestamp;

    public AvgData(String id, String type, double value, long timestamp) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
