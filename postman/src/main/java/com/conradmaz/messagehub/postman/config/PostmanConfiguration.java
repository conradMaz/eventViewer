package com.conradmaz.messagehub.postman.config;

import com.conradmaz.messagehub.messagehubcore.config.BrokerConfig;

public class PostmanConfiguration {

    private String name;
    private int timeToComplete;
    private BrokerConfig broker;
    private RouteConfig route;
    private int healthCheckReadyPort;

    public RouteConfig getRoute() {
        return route;
    }

    public void setRoute(RouteConfig route) {
        this.route = route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(int timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    public BrokerConfig getBroker() {
        return broker;
    }

    public void setBroker(BrokerConfig broker) {
        this.broker = broker;
    }
    
    
    public void setHealthCheckReadyPort(int healthCheckPort) {
        this.healthCheckReadyPort = healthCheckPort;
    }

    public int getHealthCheckReadyPort() {
        return healthCheckReadyPort;
    }

    @Override
    public String toString() {
        return String.format("PostmanConfiguration [name=%s, timeToComplete=%s, broker=%s, routes=%s, healthCheckReadyPort=%s]", name, timeToComplete, broker, route, healthCheckReadyPort);
    }
}
