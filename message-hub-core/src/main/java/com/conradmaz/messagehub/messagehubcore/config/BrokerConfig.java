package com.conradmaz.messagehub.messagehubcore.config;

import java.util.List;
/**
 * The {@link BrokerConfig} is used to configure the connection Details 
 * of the Message Broker.
 * @author conrad
 *
 */
public class BrokerConfig {
    private String name;
    private List<String> hosts;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getHosts() {
        return hosts;
    }
    
    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String toString() {
        return String.format("BrokerConfig [name=%s, hosts=%s]", name, hosts);
    }

}
