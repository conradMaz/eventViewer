package com.conradmaz.messagehub.messagehubcore.endpoint.jms;

import static com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannelType.QUEUE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conradmaz.messagehub.messagehubcore.MessageHubException;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannel;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpoint;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpointConfig;
import com.conradmaz.messagehub.messagehubcore.message.DefaultMessage;
import com.conradmaz.messagehub.messagehubcore.message.Message;

public class ActiveMQMessageEndpoint implements MessageEndpoint, MessageListener, ExceptionListener {
	private ConnectionFactory connectionFactory;
	private static final Logger LOG = LoggerFactory.getLogger(ActiveMQMessageEndpoint.class);
	private final List<Message> failedMessages = new ArrayList<>();
	private Connection connection;
	private String url ;
	private MessageChannel endpoint ;
	private static final AtomicBoolean started = new AtomicBoolean(false);
	private BlockingQueue<Message> receivedMessages ;

	public ActiveMQMessageEndpoint() {
	}

	@Override
	public String getName() {
		return "ActiveMQ";
	}
	
	@Override
    public void init(MessageEndpointConfig config) {

        this.url = config.getHosts().stream()
                                    .map(host -> String.format("tcp://%s", host))
                                    .collect(Collectors.joining(","));

        connectionFactory = new ActiveMQConnectionFactory(url);
        this.endpoint =config.getDestination();
        LOG.info("Initialised gateway, gatewayName={}, url={}", getName(), url);
    }
	
	@Override
    public void start() throws MessageHubException {

        if (started.compareAndSet(false, true)) {

            try {

                LOG.info("Starting {} Gateway, hostUrl={}", getName(), url);

                connection = connectionFactory.createConnection();
                connection.start();

            } catch (JMSException e) {
                throw new MessageHubException("Failed to start ActiveMQGateway", e);
            }
        }
    }

	@Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            try {
                LOG.info("Stopping {}", getName());
                connection.close();
                connectionFactory = null;

            } catch (JMSException e) {
                LOG.error("Failed to stop ActiveMQ connection", e);
            }
        }
    }

	@Override
	public void sendMessages(BlockingQueue<Message> messages) {
		try {
		    
            
			if (endpoint.getDestinationType().equals(QUEUE)) {

				sendMessagesToQueue(messages, endpoint);
			}
		} catch (JMSException e) {
			LOG.error("Error sending messages", e);
		}
	}

    private void sendMessagesToQueue(BlockingQueue<Message> messages, MessageChannel endpoint) throws JMSException {
       Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
       
        Queue queue = session.createQueue(endpoint.getName());
        
        MessageProducer producer = session.createProducer(queue);
        while (!messages.isEmpty()) {
            sendMsg(messages.poll(), producer);
        }
    }

    private void sendMsg(Message message, MessageProducer producer) {
        
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        
        try {
            LOG.info("Sending message, message={}", message.getBody());
            textMessage.setText(message.getBody());
            producer.send(textMessage);
        
        } catch (JMSException e) {
            LOG.error("Failed to send message, message={}", message, e);
            failedMessages.add(message);
        }
    }

   

    @Override
    public void consumeMessage(BlockingQueue<Message> messages) {
        this.receivedMessages = messages;

        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            connection.setExceptionListener(this);

            Destination destination = session.createQueue(endpoint.getName());

            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);

        } catch (JMSException e) {
            LOG.error("Error consuming messages endpoint={}", endpoint.getName(), e);
        }
    }

    @Override
    public void onMessage(javax.jms.Message message) {

        if (message instanceof TextMessage) {

            Message msg;
            try {
                msg = new DefaultMessage(((TextMessage) message).getText());
                receivedMessages.offer(msg);
            } catch (JMSException e) {
                LOG.error("Error processing message, msg={}", message, e);
            }
        }
    }

    @Override
    public void onException(JMSException exception) {

    }
}
