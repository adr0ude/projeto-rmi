package com.rmi.sensors;

public class HumiditySensor implements Sensor {

    @Override
    public double readValue() {
        return Math.random() * 100;
    }
 
}