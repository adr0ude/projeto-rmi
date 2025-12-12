package com.rmi.sensors;

public class LuminositySensor implements Sensor {

    @Override
    public double readValue() {
        return Math.random() * 100;
    }


}
