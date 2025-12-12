package com.rmi.commons.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rmi.commons.model.AvgData;

public interface SensorQuery extends Remote {
    public AvgData getLastAverage(String sensorId, String sensorType) throws RemoteException;
}
