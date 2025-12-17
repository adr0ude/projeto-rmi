package com.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import com.rmi.commons.interfaces.SensorInput;
import com.rmi.controller.*;
import com.rmi.sensors.*;

public class ClientApp {

        public static void main(String[] args) {
                try {
                        // 1. Conexão com o Registry RMI
                        Registry registry = LocateRegistry.getRegistry("localhost", 3000);
                        SensorInput input = (SensorInput) registry.lookup("SensorInput");

                        // 2. Listas para armazenar múltiplos sensores de cada tipo
                        List<TemperatureController> tempSensors = new ArrayList<>();
                        List<HumidityController> humiditySensors = new ArrayList<>();
                        List<PhController> phSensors = new ArrayList<>();
                        List<LuminosityController> lightSensors = new ArrayList<>();

                        // 3. Inicialização de múltiplos sensores (Ex: 5 de cada)
                        System.out.println("Configurando rede de sensores...");
                        for (int i = 1; i <= 5; i++) {
                                tempSensors.add(new TemperatureController(new TemperatureSensor(), "thermometer-" + i));
                                humiditySensors.add(new HumidityController(new HumiditySensor(), "hygrometer-" + i));
                                phSensors.add(new PhController(new PhSensor(), "ph-meter-" + i));
                                lightSensors.add(new LuminosityController(new LuminositySensor(), "light-sensor-" + i));
                        }

                        System.out.println("Sensores prontos. Iniciando transmissão...");

                        // 4. Loop de leitura e envio
                        while (true) {
                                System.out.println("\n--- Nova rodada de leituras ---");

                                // Enviando Temperaturas
                                for (TemperatureController sensor : tempSensors) {
                                        var data = sensor.readData();
                                        System.out.println("Enviando RMI: " + data.getId() + " ["
                                                        + data.getValue() + "]");
                                        input.readValue(data);
                                }

                                // Enviando Umidades
                                for (HumidityController sensor : humiditySensors) {
                                        input.readValue(sensor.readData());
                                }

                                // Enviando pH
                                for (PhController sensor : phSensors) {
                                        input.readValue(sensor.readData());
                                }

                                // Enviando Luminosidade
                                for (LuminosityController sensor : lightSensors) {
                                        input.readValue(sensor.readData());
                                }

                                // Intervalo entre as leituras (30 segundos)
                                Thread.sleep(30000);
                        }

                } catch (java.rmi.ConnectException e) {
                        System.err.println("ERRO: O Servidor RMI não está rodando na porta 3000.");
                } catch (Exception e) {
                        System.err.println("ERRO GERAL: " + e.getMessage());
                        e.printStackTrace();
                }
        }
}