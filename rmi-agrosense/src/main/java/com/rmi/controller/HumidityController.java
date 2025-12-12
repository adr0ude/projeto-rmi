package com.rmi.controller;

import com.rmi.commons.model.SensorData;
import com.rmi.sensors.Sensor;

public class HumidityController {

    private final Sensor sensor;
    private final String sensorId;

    public HumidityController(Sensor sensor, String sensorId) {
        this.sensor = sensor;
        this.sensorId = sensorId;
    }

    public SensorData readData() {
        double value = sensor.readValue();
        return new SensorData(
                sensorId,
                "humidity",
                value,
                System.currentTimeMillis());
    }
}
