package com.conradmaz.messagehub.postman.config;

public class RouteConfig {

    private String name;
    private String routeType;
    private int numberOfMessages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    @Override
    public String toString() {
        return String.format("RouteConfig [name=%s, routeType=%s, numberOfMessages=%s]", name, routeType, numberOfMessages);
    }
    
    
}
