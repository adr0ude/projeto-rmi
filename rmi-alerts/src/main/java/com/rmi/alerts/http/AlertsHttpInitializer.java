package com.rmi.alerts.http;

import io.javalin.Javalin;
import com.rmi.alerts.services.AlertsServiceImpl;

public class AlertsHttpInitializer {

    private static final int ALERTS_HTTP_PORT = 4568;

    public void start() {

        Javalin app = Javalin.create().start(ALERTS_HTTP_PORT);

        System.out.println("Servidor HTTP de Alertas (Javalin) iniciado na porta " + ALERTS_HTTP_PORT);

        app.get("/api/v1/alerts", ctx -> {

            var history = AlertsServiceImpl.getAlertHistory();

            ctx.json(history);
        });

        app.events(event -> {
            app.options("/*", ctx -> ctx.header("Access-Control-Allow-Origin", "*"));
        });
    }
}