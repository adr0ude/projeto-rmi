package com.rmi.server.http;

import io.javalin.Javalin;
import com.rmi.server.model.SensorDatabase;

public class HttpServerInitializer {

    private final SensorDatabase db;
    private static final int HTTP_PORT = 4567;

    public HttpServerInitializer() {
        this.db = SensorDatabase.getInstance();
    }

    public void start() {

        Javalin app = Javalin.create().start(HTTP_PORT);

        System.out.println("Servidor HTTP/JSON (Javalin) iniciado na porta " + HTTP_PORT);

        app.get("/api/v1/averages", ctx -> {
            var averages = db.getAllLastAverages();
            ctx.json(averages);
        });

        app.get("/status", ctx -> {
            ctx.result("Servidor SPS (RMI & HTTP) funcionando corretamente.");
        });

        app.get("/api/v1/averages/{sensorId}/{type}", ctx -> {

            String sensorId = ctx.pathParam("sensorId");

            String sensorType = ctx.pathParam("type");
            var avgData = db.getLastAverage(sensorId, sensorType);

            if (avgData != null) {
                ctx.json(avgData);
            } else {
                ctx.status(404)
                        .result("Média para o sensor " + sensorId + " e tipo " + sensorType + " não encontrada.");
            }
        });
    }
}