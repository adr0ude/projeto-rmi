package com.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.rmi.commons.interfaces.SensorInput;
import com.rmi.controller.*;
import com.rmi.sensors.*;

public class ClientApp {
        public static void main(String[] args) throws Exception {
                try {
                        Registry registry = LocateRegistry.getRegistry("localhost", 3000);
                        SensorInput input = (SensorInput) registry.lookup("SensorInput");

                        String clientId = "client-001";

                        TemperatureSensor tempSensor = new TemperatureSensor();
                        HumiditySensor humiditySensor = new HumiditySensor();
                        PhSensor phSensor = new PhSensor();
                        LuminositySensor lightSensor = new LuminositySensor();

                        TemperatureController tempController = new TemperatureController(tempSensor, clientId);

                        HumidityController humidityController = new HumidityController(humiditySensor, clientId);

                        PhController phController = new PhController(phSensor, clientId);

                        LuminosityController lightController = new LuminosityController(lightSensor, clientId);

                        System.out.println("Iniciando sensores...");
                        while (true) {

                                System.out.println("Enviando leitura de temperatura...");
                                input.readValue(tempController.readData());

                                System.out.println("Enviando leitura de umidade...");
                                input.readValue(humidityController.readData());

                                System.out.println("Enviando leitura de pH...");
                                input.readValue(phController.readData());

                                System.out.println("Enviando leitura de luminosidade...");
                                input.readValue(lightController.readData());

                                Thread.sleep(30000);
                        }
                } catch (java.rmi.ConnectException e) {
                        System.err.println(
                                        "ERRO RMI: Falha ao conectar. O Servidor PSP (ServerApp) está rodando na porta 3000?");
                } catch (java.rmi.NotBoundException e) {
                        System.err.println("ERRO RMI: Serviço 'SensorInput' não encontrado no Registry.");
                } catch (Exception e) {
                        System.err.println("ERRO GERAL NO CLIENTE: " + e.getMessage());
                        e.printStackTrace();
                }
        }
}
