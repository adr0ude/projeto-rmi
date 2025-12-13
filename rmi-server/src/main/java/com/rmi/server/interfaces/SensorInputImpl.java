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
        String alertaMsg = null;

        if (type.equals("temperature")) {
            if (value > 40.0) {
                alertaMsg = "CRÍTICO! Temperatura (" + value + "°C) muito alta. Acima de 40°C.";
            } else if (value >= 32.0) {
                alertaMsg = "ATENÇÃO! Temperatura elevada (" + value + "°C). Faixa de 32°C a 40°C.";
            }
        } else if (type.equals("humidity")) {
            if (value < 30.0) {
                alertaMsg = "Umidade muito baixa (" + value + "%). Risco de ressecamento.";
            } else if (value > 80.0) {
                alertaMsg = "Umidade muito alta (" + value + "%). Risco de fungos.";
            }
        } else if (type.equals("ph")) {
            String situacao;

            if (value < 5.5) {
                situacao = "Solo muito ácido";
            } else if (value < 6.5) {
                situacao = "Solo levemente ácido";
            } else if (value <= 7.5) {
                situacao = "Solo neutro";
            } else {
                situacao = "Solo alcalino";
            }

            if (value < 5.5 || value > 7.5) {
                alertaMsg = "ALERTA pH! Média (" + value + "). Classificação: " + situacao;
            }

            System.out.println("[LOG CAT] PH Médio: " + value + ". Situação do Solo: " + situacao);
        } else if (type.equals("luminosity")) {
            if (value < 100.0) {
                alertaMsg = "Baixa Luminosidade (" + value + "). Necessário acionar luzes de apoio.";
            } else if (value > 900.0) {
                alertaMsg = "Alta Luminosidade (" + value + "). Necessário sombreamento.";
            }

            if (alertaMsg == null) {
                System.out.println("[LOG CAT] Luminosidade média calculada: " + value);
            }
        }

        if (alertaMsg != null) {
            System.out.println("Disparando ALERTA RMI para o AlertsService...");
            alertsService.notifyAlert(type, alertaMsg);
        }
    }
}