package com.rmi.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rmi.commons.model.AvgData;
import com.rmi.commons.model.SensorData;

public class SensorDatabase {
    private static SensorDatabase instance = null;

    private final Map<String, List<SensorData>> buffers = new HashMap<>();
    private final Map<String, AvgData> readyToConsume = new HashMap<>();
    private final Map<String, AvgData> lastKnownAverage = new HashMap<>();

    private static final int BUFFER_SIZE = 5;

    private SensorDatabase() {
    }

    public static synchronized SensorDatabase getInstance() {
        if (instance == null) {
            instance = new SensorDatabase();
        }
        return instance;
    }

    private String getBufferKey(SensorData data) {
        return data.getId() + ":" + data.getType();
    }

    public synchronized void addReading(SensorData data) {
        String key = getBufferKey(data);

        buffers.putIfAbsent(key, new ArrayList<>());

        List<SensorData> buffer = buffers.get(key);
        buffer.add(data);

        if (buffer.size() == BUFFER_SIZE) {
            double avg = buffer.stream()
                    .mapToDouble(SensorData::getValue)
                    .average()
                    .orElse(0.0);

            AvgData avgData = new AvgData(
                    data.getId(),
                    data.getType(),
                    avg,
                    System.currentTimeMillis());

            readyToConsume.put(key, avgData);
            lastKnownAverage.put(key, avgData);

            buffers.put(key, new ArrayList<>());
        }
    }

    public synchronized AvgData getAverage(String sensorId, String sensorType) {
        String key = sensorId + ":" + sensorType;
        return readyToConsume.remove(key);
    }

    public synchronized AvgData getLastAverage(String sensorId, String sensorType) {
        String key = sensorId + ":" + sensorType;
        return lastKnownAverage.get(key);
    }

    public synchronized Map<String, AvgData> getAllLastAverages() {
        return new HashMap<>(lastKnownAverage);
    }
}