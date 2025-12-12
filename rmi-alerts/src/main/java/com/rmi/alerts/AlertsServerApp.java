package com.rmi.alerts;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.rmi.commons.interfaces.AlertsControl;
import com.rmi.alerts.interfaces.AlertsControlImpl;

public class AlertsServerApp {

    private static final int RMI_PORT = 3001;

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Servidor RMI de Alertas na porta " + RMI_PORT);

            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            AlertsControl alertsService = new AlertsControlImpl();

            registry.rebind("AlertsService", alertsService);

            System.out.println("Serviço de Alertas RMI (AlertsService) pronto.");

        } catch (Exception e) {
            System.err.println("Erro ao iniciar o Alerts Server:");
            e.printStackTrace();
        }
    }
}