package com.rmi.server;

import java.rmi.registry.*;

import com.rmi.commons.interfaces.*;
import com.rmi.server.interfaces.SensorInputImpl;
import com.rmi.server.interfaces.SensorQueryImpl;
import com.rmi.server.http.HttpServerInitializer;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        try {
            Registry registry = LocateRegistry.createRegistry(3000);
            SensorInput inputService = new SensorInputImpl();
            SensorQuery queryService = new SensorQueryImpl();
            registry.rebind("SensorInput", inputService);
            registry.rebind("SensorQuery", queryService);
            System.out.println("Servidor RMI iniciado...");

            HttpServerInitializer httpService = new HttpServerInitializer();
            httpService.start();

            System.out.println("Servidor SPS (RMI & HTTP) pronto.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
