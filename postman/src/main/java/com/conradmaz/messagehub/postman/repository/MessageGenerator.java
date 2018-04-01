package com.conradmaz.messagehub.postman.repository;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.conradmaz.messagehub.messagehubcore.CompletedListener;
import com.conradmaz.messagehub.messagehubcore.MessageHubComponent;
import com.conradmaz.messagehub.messagehubcore.MessageHubException;
import com.conradmaz.messagehub.messagehubcore.NotifyCompletedSupport;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageChannel;
import com.conradmaz.messagehub.messagehubcore.endpoint.MessageEndpoint;
import com.conradmaz.messagehub.messagehubcore.message.DefaultMessage;
import com.conradmaz.messagehub.messagehubcore.message.Message;
import com.conradmaz.messagehub.postman.config.RouteConfig;

public class MessageGenerator implements MessageHubComponent, NotifyCompletedSupport{

    private static final Logger LOG = LoggerFactory.getLogger(MessageGenerator.class);
    private final int numberOfMessages;
    private final MessageEndpoint messageEndpoint;
    private final BlockingQueue<Message> queue;
    private final ScheduledExecutorService schExService;
    private final BigInteger numberOfMessagesPerMin;
    private final AtomicInteger messagesCounter = new AtomicInteger();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private CompletedListener observer ;
    private String name;
    private MessageChannel destination;

    public MessageGenerator(int timeToComplete, MessageEndpoint messageEndpoint, RouteConfig route) {
        this.numberOfMessages = route.getNumberOfMessages();
        this.messageEndpoint = messageEndpoint;
        this.numberOfMessagesPerMin = getNumberOfMessagesPerMin((timeToComplete == 0) ? 1 : timeToComplete);
        this.queue = new LinkedBlockingQueue<>(numberOfMessagesPerMin.intValue());
        this.schExService = Executors.newScheduledThreadPool(1);
        this.name = String.format("%s-%s-%s", route.getName(), route.getRouteType(), "generator");
    }


    @Override
    public void start() throws MessageHubException {
        
        if (isRunning.compareAndSet(false, true)) {
            
            LOG.info("Starting MessageGenerator name={}, numberofMessages={}, numberOfMessagesPerMin={}",name, numberOfMessages, numberOfMessagesPerMin);
            
            schExService.scheduleAtFixedRate(() -> sendMessages(), 0, 1, TimeUnit.MINUTES);
        }
    }
    
    @Override
    public void register(CompletedListener observer) {
        this.observer = observer;
        
    }

    @Override
    public void notifyCompleted() {
       observer.completed(this);
        
    }

    public String getName() {
        return name;
    }

    @Override
    public void stop() throws MessageHubException {
        
        if (isRunning.compareAndSet(true, false)) {
            
            LOG.info("Stopping MessageGenerator, name={}, totalMessageGenerated={}",name, messagesCounter.getAndIncrement());
            
            schExService.shutdown();
            
            try {
                if (!schExService.awaitTermination(1, TimeUnit.SECONDS)) {
                    schExService.shutdownNow();
                }

            } catch (InterruptedException e) {
                LOG.warn("Interrupted", e);
            }
        } else {
            LOG.info("MessageGenerator not running");
        }
    }
    
    private void sendMessages() {

        try {
            for (int i = 0; i < numberOfMessagesPerMin.intValue(); i++) {
                queue.offer(new DefaultMessage(String.format("Postman message %s", messagesCounter.getAndIncrement())));
            }

            messageEndpoint.sendMessages(queue);

        } catch (InterruptedException e) {
            LOG.error("Thread Interrupted, route={}", destination.getName());
            Thread.currentThread().interrupt();
        }
        
        if (messagesCounter.get() >= numberOfMessages) {
            LOG.info("MessageGenerator complete, route={}, numberOfMessagesSent={}", destination.getName(), numberOfMessages);
            notifyCompleted();
        }
    }

    
    private BigInteger getNumberOfMessagesPerMin(int rate) {
        BigInteger numberOfMessagesPerMinute = BigInteger.ONE;
        if (numberOfMessages > rate) {
            BigInteger.valueOf(numberOfMessages).divide(BigInteger.valueOf(rate));
        }
        return numberOfMessagesPerMinute;
    }
}
