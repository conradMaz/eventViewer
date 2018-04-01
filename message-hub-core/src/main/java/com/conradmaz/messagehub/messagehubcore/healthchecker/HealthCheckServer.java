package com.conradmaz.messagehub.messagehubcore.healthchecker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HealthCheckServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckServer.class);

    private int healthCheckPort;
    private ServerSocket serverSocket;
    private String applicationName;

    protected HealthCheckServer(String applicationName, int healthCheckPort) {
        this.applicationName = applicationName;
        this.healthCheckPort = healthCheckPort;
    }

    @Override
    public void run() {
        try {
            LOG.info("HealthCheckServer started, port={}, instanceName={}", healthCheckPort, applicationName);

            serverSocket = new ServerSocket(healthCheckPort);
            Socket clientSocket = serverSocket.accept();

            HealthCheckClient healthCheckClient = new HealthCheckClient(clientSocket, applicationName);
            healthCheckClient.start();

        } catch (IOException e) {
            LOG.error("HealthCheckServer failed to start", e);
        }
    }

    protected void close() throws IOException {
        serverSocket.close();
    }
}
