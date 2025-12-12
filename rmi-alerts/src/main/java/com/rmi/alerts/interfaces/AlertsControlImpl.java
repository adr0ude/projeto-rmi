package com.rmi.alerts.interfaces;

import com.rmi.commons.interfaces.AlertsControl;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AlertsControlImpl extends UnicastRemoteObject implements AlertsControl {

    public AlertsControlImpl() throws RemoteException {
        super();
        System.out.println("Serviço de Atuadores iniciado e esperando callbacks RMI...");
    }

    @Override
    public void notifyAlert(String type, String message) throws RemoteException {
        System.err.println("\nALERTA RECEBIDO POR RMI (CALLBACK)");
        System.err.println("  - Tipo de Sensor: " + type.toUpperCase());
        System.err.println("  - Ação Necessária: " + message);
        System.err.println("----------------------------------------\n");
    }
}