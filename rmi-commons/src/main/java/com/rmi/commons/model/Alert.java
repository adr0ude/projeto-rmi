package com.rmi.commons.model;

import java.io.Serializable;

public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String message;
    private long timestamp;

    public Alert(String type, String message, long timestamp) {
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}