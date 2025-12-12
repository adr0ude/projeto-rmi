package com.rmi.alerts.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.rmi.commons.interfaces.AlertsControl;

import com.rmi.commons.model.Alert;

public class AlertsServiceImpl extends UnicastRemoteObject implements AlertsControl {

    private static final List<Alert> alertHistory = new ArrayList<>();

    public AlertsServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void notifyAlert(String sensorType, String message) throws RemoteException {
        Alert newAlert = new Alert(sensorType, message, System.currentTimeMillis());

        alertHistory.add(0, newAlert);

        System.out.println("[ALARM RECEBIDO] Tipo: " + sensorType + " | Mensagem: " + message);

        if (alertHistory.size() > 100) {
            alertHistory.remove(alertHistory.size() - 1);
        }
    }

    public static List<Alert> getAlertHistory() {
        return alertHistory;
    }
}
