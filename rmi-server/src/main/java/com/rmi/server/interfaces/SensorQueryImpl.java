package com.rmi.server.interfaces;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import com.rmi.commons.interfaces.SensorQuery;
import com.rmi.commons.model.AvgData;
import com.rmi.server.model.SensorDatabase;

public class SensorQueryImpl extends UnicastRemoteObject implements SensorQuery {

    private final SensorDatabase db;

    public SensorQueryImpl() throws Exception {
        this.db = SensorDatabase.getInstance();
    }

    @Override
    public AvgData getLastAverage(String sensorId, String sensorType) throws RemoteException {
        return db.getLastAverage(sensorId, sensorType);
    }
}
