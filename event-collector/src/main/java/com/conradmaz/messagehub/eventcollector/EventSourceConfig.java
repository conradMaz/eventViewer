package com.conradmaz.messagehub.eventcollector;

public class EventSourceConfig {

    private String name;
    private String sourceType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "EventSourceConfig [source=" + name + ", sourceType=" + sourceType + "]";
    }
}
