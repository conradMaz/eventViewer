package com.conradmaz.messagehub.postman;

import static com.conradmaz.messagehub.utils.LambdaExceptionUtil.rethrowConsumer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conradmaz.messagehub.messagehubcore.CompletedListener;
import com.conradmaz.messagehub.messagehubcore.MessageHubComponent;
import com.conradmaz.messagehub.messagehubcore.MessageHubException;
import com.conradmaz.messagehub.messagehubcore.NotifyCompletedSupport;
import com.conradmaz.messagehub.messagehubcore.config.ConfigLoader;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannel;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannelType;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpoint;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpointConfig;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpointFactory;
import com.conradmaz.messagehub.messagehubcore.healthchecker.HealthChecker;
import com.conradmaz.messagehub.postman.config.PostmanConfiguration;
import com.conradmaz.messagehub.postman.config.RouteConfig;
import com.conradmaz.messagehub.postman.repository.MessageGenerator;

/**
 * The {@link Postman} application will be responsible for posting messages to a
 * {@link MessageEndpoint}.
 * <p>
 * The {@link PostmanConfiguration} is used to load the postman config which can
 * be configured using in the below file:
 * 
 * <pre>
 * config/postman.yml
 * </pre>
 * 
 * </p>
 * 
 * @author conrad
 *
 */
public class Postman implements MessageHubComponent, CompletedListener {

    private static final Logger LOG = LoggerFactory.getLogger(Postman.class);
    private PostmanConfiguration config;
    private MessageEndpoint messageEndpoint;
    private String name;
    private MessageGenerator messageGenerator;
    private  Optional<HealthChecker> healthChecker = Optional.empty();
    private static final ConfigLoader<PostmanConfiguration> configLoader = new ConfigLoader<>();
	
    public static void main(String[] args) {

        Postman postman = new Postman();

        postman.init();
        postman.start();
    }

    /**
     * Initialise the {@link Postman}
     */
    public void init() {

        LOG.info("Initialising Postman");

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOG.error("Error, threadName={}, error={}", t.getName(), e.getMessage(), e));
        Runtime.getRuntime().addShutdownHook(new Thread(()->stop()));

        this.config = configLoader.loadApplicationConfig("postman",PostmanConfiguration.class);

        this.name = config.getName();
        loadMessageEndpoint();

        this.messageGenerator = createMessageGenerator(config.getTimeToComplete(), config.getRoute());
        initialiseHealthChecker();

        LOG.info("Postman intialised");
    }

    private void initialiseHealthChecker() {
        try {
            
            if(config.getHealthCheckReadyPort()>0) {
                HealthChecker hc = new HealthChecker();
                hc.init(config.getHealthCheckReadyPort(), getName());
                healthChecker = Optional.of(hc);
            }
        } catch (MessageHubException e) {
            LOG.error("Failed to initialise healthChecker", e);
        }
    }
    
	/**
	 * Start the {@link Postman}
	 */
    @Override
    public void start() {
        
        try {
            LOG.info("Starting Postman, postmanName={}, timeToComplete={}", getName(), config.getTimeToComplete());
            
            messageEndpoint.start();
            healthChecker.ifPresent(rethrowConsumer(HealthChecker::start));
            messageGenerator.start();
            
        } catch (MessageHubException e) {
            throw new IllegalStateException(String.format("Error starting Postman, name=%s, error=%s", getName(), e.getMessage()),e);
        }
    }
    /**
     * Stop the {@link Postman}
     */

    @Override
    public void stop() {

        LOG.info("Stopping postman, postmanName={}", getName());

        try {
            messageGenerator.stop();

            messageEndpoint.stop();
            healthChecker.ifPresent(rethrowConsumer(HealthChecker::stop));

        } catch (MessageHubException e) {
            throw new IllegalStateException("Failed to stop Postman", e);
        }
    }
    

    @Override
    public void completed(NotifyCompletedSupport notifyCompletedInstance) {

        if (notifyCompletedInstance instanceof MessageGenerator) {
            MessageGenerator mg = (MessageGenerator) notifyCompletedInstance;

            LOG.info("MessageGenerator notified completed, name={}", mg.getName());
        }
    }

    private String getName() {
        return name;
    }

    private void loadMessageEndpoint() {
        
        Optional<MessageEndpoint> gatewayOpt = MessageEndpointFactory.getInstance().getMessageEndpoint(config.getBroker().getName());
        String errorMessage = String.format("Message Endpoint '%s'not found", config.getBroker().getName());
       
        this.messageEndpoint = gatewayOpt.orElseThrow(() -> new IllegalStateException(errorMessage));
        MessageChannel channel = new MessageChannel(MessageChannelType.getType(config.getRoute().getRouteType()), config.getRoute().getName());

        this.messageEndpoint.init(new MessageEndpointConfig(config.getBroker().getHosts(),channel) );
    }

    private MessageGenerator createMessageGenerator(int timeToComplete, RouteConfig route) {

        MessageGenerator mg = new MessageGenerator(timeToComplete, messageEndpoint, route);
        mg.register(this);

        LOG.info("Created message generator, messageGenerator={}, route={}", mg.getName(), route);

        return mg;
    }
}
