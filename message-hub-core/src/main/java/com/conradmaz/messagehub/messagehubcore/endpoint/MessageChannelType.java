package com.conradmaz.messagehub.messagehubcore.endpoint;

public enum MessageChannelType {
QUEUE,TOPIC;

public static MessageChannelType getType(String destinationType) {
    if(QUEUE.name().equals(destinationType.toUpperCase())) {
        return QUEUE;
    }
    else if(TOPIC.name().equals(destinationType.toUpperCase())) {
        return TOPIC;
    }
    return null;
}
}
