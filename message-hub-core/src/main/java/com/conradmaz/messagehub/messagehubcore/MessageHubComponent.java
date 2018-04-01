package com.conradmaz.messagehub.messagehubcore;

/**
 * A {@link MessageHubComponent} is a component that belongs to the message hub
 * 
 *
 */
public interface MessageHubComponent {

    /**
     * Starts the component
     */
    void start() throws MessageHubException;

    /**
     * Stop the component
     */
    void stop() throws MessageHubException;
}
