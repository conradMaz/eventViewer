package com.conradmaz.messagehub.messagehubcore.endpoint;

public class MessageChannel {

    private MessageChannelType destinationType;
    private String name;

    public MessageChannel(MessageChannelType destinationType, String name) {
        this.destinationType = destinationType;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MessageChannelType getDestinationType() {
        return destinationType;
    }

}
