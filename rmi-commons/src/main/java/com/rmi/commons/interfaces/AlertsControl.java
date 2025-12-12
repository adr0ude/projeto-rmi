package com.rmi.commons.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AlertsControl extends Remote {
    void notifyAlert(String type, String message) throws RemoteException;
}