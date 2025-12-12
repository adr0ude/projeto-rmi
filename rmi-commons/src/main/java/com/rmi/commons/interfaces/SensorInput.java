package com.rmi.commons.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.rmi.commons.model.SensorData;

public interface SensorInput extends Remote {

    void readValue(SensorData data) throws RemoteException;
}
