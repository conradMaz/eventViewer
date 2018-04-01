package com.conradmaz.messagehub.messagehubcore.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads all the supported
 * 
 * @author conrad
 *
 */
public class MessageEndpointFactory {
	private final List<MessageEndpoint> messageGateways = new CopyOnWriteArrayList<>();
	private static MessageEndpointFactory instance;
	private static final String CONFIG_FILE = "message-gateways.properties";
	private final Properties properties = new Properties();
	private static Logger LOG = LoggerFactory.getLogger(MessageEndpointFactory.class);

	private MessageEndpointFactory() {
		loadGateways();
	}

	public static synchronized MessageEndpointFactory getInstance() {
		if (instance == null) {
			instance = new MessageEndpointFactory();
		}
		return instance;
	}

	private void loadGateways() {

		try {
			InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            properties.load(resourceAsStream);
			properties.entrySet().forEach(this::loadMessageEndpoint);
			
		} catch (IOException e) {
			LOG.error("Error loading gateways", e);
		}
	}

    private void loadMessageEndpoint(Entry<Object, Object> entry)  {
        try {
            MessageEndpoint gateway = null;

            Class<MessageEndpoint> gatewayClass = (Class<MessageEndpoint>) Class.forName((String) entry.getValue());
            gateway = gatewayClass.newInstance();
            messageGateways.add(gateway);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to load messageGateway for %s", entry.getValue()), e);
        }
    }

	public Optional<MessageEndpoint> getMessageEndpoint(String name) {

           return  messageGateways.stream()
                               .filter(gateway -> gateway.getName().equalsIgnoreCase(name))
                               .findFirst();

	}
}
