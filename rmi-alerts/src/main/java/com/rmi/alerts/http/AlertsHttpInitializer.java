package com.rmi.alerts.http;

import io.javalin.Javalin;
import com.rmi.alerts.services.AlertsServiceImpl;

import io.javalin.plugin.bundled.CorsPluginConfig;

public class AlertsHttpInitializer {

    private static final int ALERTS_HTTP_PORT = 4568;

    public void start() {

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });
        }).start(ALERTS_HTTP_PORT);

        System.out.println("Servidor HTTP de Alertas (Javalin) iniciado na porta " + ALERTS_HTTP_PORT);

        app.get("/api/v1/alerts", ctx -> {
            ctx.json(AlertsServiceImpl.getAlertHistory());
        });

        app.events(event -> {
            app.options("/*", ctx -> ctx.header("Access-Control-Allow-Origin", "*"));
        });
    }
}