package com.rmi.sensors;

public class PhSensor implements Sensor {

    @Override
    public double readValue() {
        return Math.random() * 14;
    }
    
}

