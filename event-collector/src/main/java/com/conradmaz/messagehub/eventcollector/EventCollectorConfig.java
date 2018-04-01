package com.conradmaz.messagehub.eventcollector;

import com.conradmaz.messagehub.messagehubcore.config.BrokerConfig;
/**
 * {@link EventCollectorConfig} is the config which will be loaded to the 
 * {@link EventCollector}
 * @author conrad
 *
 */
public class EventCollectorConfig {

    private BrokerConfig broker;
    private EventSourceConfig eventSource;
    private String name;
    private int livenessProbe;

    public EventSourceConfig getEventSource() {
        return eventSource;
    }

    public void setEventSource(EventSourceConfig eventSource) {
        this.eventSource = eventSource;
    }

    public BrokerConfig getBroker() {
        return broker;
    }

    public void setBroker(BrokerConfig broker) {
        this.broker = broker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public int getLivenessProbe() {
        return livenessProbe;
    }

    
    public void setLivenessProbe(int livenessProbe) {
        this.livenessProbe = livenessProbe;
    }

    @Override
    public String toString() {
        return "EventCollectorConfig [broker=" + broker + ", eventSource=" + eventSource + ", name=" + name + ", livenessProbe=" + livenessProbe
                + "]";
    }

}
