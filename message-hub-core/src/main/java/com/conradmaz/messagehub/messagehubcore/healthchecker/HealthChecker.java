package com.conradmaz.messagehub.messagehubcore.healthchecker;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conradmaz.messagehub.messagehubcore.MessageHubComponent;
import com.conradmaz.messagehub.messagehubcore.MessageHubException;

/**
 * The {@link HealthChecker} is used to check the health of an application.
 * <p>
 * The Application notifies when it is started which will create a
 * {@linkplain ServerSocket} to listens for a connection on the port provided by
 * the application
 * </p>
 * 
 * @author conrad
 *
 */
public class HealthChecker implements MessageHubComponent {

    private static final Logger LOG = LoggerFactory.getLogger(HealthChecker.class);
    private int healthCheckPort;
    private HealthCheckServer healthCheckServer;
    private String applicationName;
    private static final AtomicBoolean started = new AtomicBoolean(false);

    public HealthChecker() {
    }
   

    public void init(int port, String application) throws MessageHubException {

        if (port < 1024) {
            throw new MessageHubException("Port numbers 0 -1023 are reserved port numbers");
        }

        this.healthCheckPort = port;
        this.applicationName = application;
    }

    @Override
    public void start() throws MessageHubException {

        if (started.compareAndSet(false, true)) {

            LOG.info("Starting the HealthChecker, port={}", healthCheckPort);

            if (healthCheckPort != 0) {

                healthCheckServer = new HealthCheckServer(applicationName, healthCheckPort);
                healthCheckServer.start();
            }
        }
    }
    

    @Override
    public void stop() throws MessageHubException {
       
        if (started.compareAndSet(true, false)) {
            LOG.info("Stopping HealthChecker");
            try {
                healthCheckServer.close();
            } catch (IOException e) {
                throw new MessageHubException("Failed to stop HealthCheker", e);
            }
        }
    }
}
