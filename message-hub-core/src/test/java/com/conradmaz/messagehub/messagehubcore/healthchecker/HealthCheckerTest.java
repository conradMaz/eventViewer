package com.conradmaz.messagehub.messagehubcore.healthchecker;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.conradmaz.messagehub.messagehubcore.MessageHubException;

public class HealthCheckerTest {

    private static final String HEATH_CHECK_TESTER = "HeathCheckTester";

    private HealthChecker healthChecker = new HealthChecker();

    private static final int PORT_2455 = 2455;

    @Before
    public void setup() throws MessageHubException {
        healthChecker.init(PORT_2455, HEATH_CHECK_TESTER);
    }

    @After
    public void tearDown() throws MessageHubException {
        healthChecker.stop();
    }

    @Test
    public void healthChecker_throws_exception_when_port_initalised_with_less_than_1023() throws MessageHubException {
        // given

        // when
        assertThatThrownBy(() -> healthChecker.init(0, HEATH_CHECK_TESTER)).isInstanceOf(MessageHubException.class);

        // then
        // MessageHubException is thrown

    }

    @Test
    public void connection_refused_to_healthCheck_port_when_healthChecker_not_started() throws MessageHubException {
        
        assertThatThrownBy(() -> new Socket("localhost", 2455)).isInstanceOf(IOException.class);
    }

    @Test
    public void connection_accepted_to_healthCheck_port_when_healthChecker_started() throws MessageHubException, UnknownHostException, IOException {

        // given
        healthChecker.start();

        // then
        assertThatCode(() -> {
            connectToHealthCheckPort();

        }).doesNotThrowAnyException();
    }

    private void connectToHealthCheckPort() {
        
            Socket clientSocket;
            try {
                clientSocket = new Socket("localhost", PORT_2455);
                clientSocket.close();
            } catch (IOException e) {
             throw new IllegalStateException("Connection refused", e);
            }
      
    }

    @Test
    public void connection_refused_when_healthChecker_stops() throws MessageHubException {

        // given
        healthChecker.start();
        
        await().atMost(2, SECONDS).untilAsserted(() -> assertThatCode(() -> {
            Socket clientSocket = new Socket("localhost", PORT_2455);
             clientSocket.close();
        }).doesNotThrowAnyException());
        // when
        
        healthChecker.stop();

        // then
        assertThatThrownBy(() -> new Socket("localhost", 2455)).isInstanceOf(IOException.class);
    }
}
