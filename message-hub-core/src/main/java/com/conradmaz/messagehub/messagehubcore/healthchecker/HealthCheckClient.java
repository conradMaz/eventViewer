package com.conradmaz.messagehub.messagehubcore.healthchecker;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HealthCheckClient extends Thread {

    private final Socket clientSocket;
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckClient.class);
    private final String message;

    protected HealthCheckClient(Socket clientSocket, String healthCheckMessage) {
        this.clientSocket = clientSocket;
        this.message = healthCheckMessage + " is Alive";
    }

    @Override
    public void run() {
        try {
            LOG.info(message);

        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.error("Failed to close healthCheck client socket", e);
            }
        }
    }
}
