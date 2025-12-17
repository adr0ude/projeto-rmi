package com.rmi.server.interfaces;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.rmi.commons.interfaces.SensorInput;
import com.rmi.commons.interfaces.AlertsControl;
import com.rmi.commons.model.SensorData;
import com.rmi.commons.model.AvgData;
import com.rmi.server.model.SensorDatabase;

public class SensorInputImpl extends UnicastRemoteObject implements SensorInput {

    private final SensorDatabase db;
    private AlertsControl alertsService;
    private static final int ALERTS_RMI_PORT = 3001;

    public SensorInputImpl() throws Exception {
        super();
        this.db = SensorDatabase.getInstance();

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ALERTS_RMI_PORT);
            this.alertsService = (AlertsControl) registry.lookup("AlertsService");
            System.out.println("Conectado ao Serviço de Alerta RMI (AlertsService) na porta " + ALERTS_RMI_PORT);
        } catch (Exception e) {
            System.err.println("Não foi possível conectar ao AlertsService. Alertas desativados.");
            System.err.println("Certifique-se de que rmi-alerts está rodando na porta " + ALERTS_RMI_PORT);
            this.alertsService = null;
        }
    }

    @Override
    public void readValue(SensorData data) throws RemoteException {

        System.out.printf("[LOG] Dado recebido de %s: %s = %.2f\n",
                data.getId(), data.getType(), data.getValue());

        db.addReading(data);

        AvgData calculatedAvg = db.getAverage(data.getId(), data.getType());

        if (calculatedAvg != null) {
            handleAlerts(calculatedAvg);
        }
    }

    private void handleAlerts(AvgData avg) throws RemoteException {
        if (alertsService == null)
            return;

        String type = avg.getType();
        double value = avg.getValue();
        String sensorId = avg.getId(); // Capturamos o ID do sensor que gerou a média
        String alertaMsg = null;

        // 1. Padronização da Temperatura
        if (type.equals("temperature")) {
            if (value > 40.0) {
                alertaMsg = "ALERTA: Temperatura CRÍTICA detectada no sensor " + sensorId + ": " + value
                        + "°C. Acima de 40°C.";
            } else if (value >= 32.0) {
                alertaMsg = "ALERTA: Temperatura elevada detectada no sensor " + sensorId + ": " + value + "°C.";
            }
        }
        // 2. Padronização da Umidade (Igual à sua segunda imagem)
        else if (type.equals("humidity")) {
            if (value < 30.0) {
                alertaMsg = "ALERTA: Umidade muito baixa detectada no sensor " + sensorId + ": " + value + "%";
            } else if (value > 80.0) {
                alertaMsg = "ALERTA: Umidade muito alta detectada no sensor " + sensorId + ": " + value + "%";
            }
        }
        // 3. Padronização do pH
        else if (type.equals("ph")) {
            if (value < 5.5 || value > 7.5) {
                String situacao = (value < 5.5) ? "Solo muito ácido" : "Solo alcalino";
                alertaMsg = "ALERTA: pH fora da faixa ideal no sensor " + sensorId + ": " + value + " (" + situacao
                        + ")";
            }
        }
        // 4. Padronização da Luminosidade
        else if (type.equals("luminosity")) {
            if (value < 100.0) {
                alertaMsg = "ALERTA: Baixa luminosidade detectada no sensor " + sensorId + ": " + value
                        + ". Acionar luzes.";
            } else if (value > 900.0) {
                alertaMsg = "ALERTA: Alta luminosidade detectada no sensor " + sensorId + ": " + value
                        + ". Sombreamento necessário.";
            }
        }

        if (alertaMsg != null) {
            System.out.println("[RMI] Disparando alerta padronizado para: " + sensorId);
            alertsService.notifyAlert(type, alertaMsg);
        }
    }
}