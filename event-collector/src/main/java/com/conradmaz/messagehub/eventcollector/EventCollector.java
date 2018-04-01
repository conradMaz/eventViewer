package com.conradmaz.messagehub.eventcollector;

import static com.conradmaz.messagehub.utils.LambdaExceptionUtil.rethrowConsumer;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conradmaz.messagehub.messagehubcore.MessageHubComponent;
import com.conradmaz.messagehub.messagehubcore.MessageHubException;
import com.conradmaz.messagehub.messagehubcore.config.ConfigLoader;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannel;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannelType;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpoint;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpointConfig;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpointFactory;
import com.conradmaz.messagehub.messagehubcore.healthchecker.HealthChecker;
import com.conradmaz.messagehub.messagehubcore.message.Message;

/**
 * The {@link EventCollector} is a {@link MessageHubComponent} that will be
 * responsible for consuming message events.
 * 
 * @author conrad
 *
 */
public class EventCollector implements MessageHubComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EventCollector.class);
    private MessageEndpoint messageEndpoint;
    private String name;
    private BlockingQueue<Message> events = new LinkedBlockingQueue<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Thread eventReceiver = new Thread(this::receiveEvents);
    private EventCollectorConfig config;
    private ConfigLoader<EventCollectorConfig> configLoader = new ConfigLoader<>();
    private Optional<HealthChecker> healthCheckerOpt = Optional.empty();

    public static void main(String[] args) {
        EventCollector eventCollector = new EventCollector();
        eventCollector.init();
        eventCollector.start();
    }

    public void init() {

        config = configLoader.loadApplicationConfig("event-collector", EventCollectorConfig.class);
        name = config.getName();

        messageEndpoint = MessageEndpointFactory.getInstance().getMessageEndpoint(config.getBroker().getName())
                .orElseThrow(() -> new IllegalStateException("Failed to laad Broker, broker=" + config.getBroker().getName()));

        MessageChannel channel = new MessageChannel(MessageChannelType.getType(config.getEventSource().getSourceType()),
                config.getEventSource().getName());

        messageEndpoint.init(new MessageEndpointConfig(config.getBroker().getHosts(), channel));

        if (config.getLivenessProbe() > 0) {
            HealthChecker healthChecker = new HealthChecker();
            try {
                healthChecker.init(config.getLivenessProbe(), name);
            } catch (MessageHubException e) {
                throw new IllegalStateException("Failed to initialise healthChecker", e);
            }

            healthCheckerOpt = Optional.of(healthChecker);
        }
    }

    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            
            LOG.info("Starting the event-collector, instanceName={}", name);
            
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOG.error("Error, threadName={}, error={}", t.getName(), e.getMessage(), e));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));

            try {
                messageEndpoint.start();
                eventReceiver.start();
                healthCheckerOpt.ifPresent(rethrowConsumer(HealthChecker::start));

            } catch (MessageHubException e1) {
                throw new IllegalStateException(String.format("Failed to start eventCollector, name=%s", name), e1);
            }
        }
    }

    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {

            LOG.info("Stopping the eventCollector, name={}", name);

            try {
                messageEndpoint.stop();
                eventReceiver.interrupt();
                healthCheckerOpt.ifPresent(rethrowConsumer(HealthChecker::stop));

            } catch (MessageHubException e) {
                LOG.error("Failed to stop eventCollector name={}", name);
            }
        }
    }

    private void receiveEvents() {

        messageEndpoint.consumeMessage(events);

        LOG.info("Started eventReceiver, eventSource={}", config.getEventSource().getName());

        while (!eventReceiver.isInterrupted()) {

            try {
                LOG.info("Receiving event, event={}", events.take().getBody());
            } catch (InterruptedException e) {
                LOG.error("Failed to receive message", e);
            }
        }
    }
}
