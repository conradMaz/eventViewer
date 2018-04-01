package com.conradmaz.messagehub.messagehubcore.endpoint;

import java.util.List;

public class MessageEndpointConfig {
    private final List<String> hosts ;
    private final MessageChannel destination ;
    
    
    public MessageChannel getDestination() {
        return destination;
    }

    

    public MessageEndpointConfig(List<String> host, MessageChannel destination) {
        this.hosts = host;
        this.destination = destination;
    }
    
    public List<String> getHosts() {
        return hosts;
    }
    
    


    @Override
    public String toString() {
        return "MessageHubServerConfig [hosts=" + hosts + ", channel=" + destination +"]+";
    }
}
