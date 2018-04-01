package com.conradmaz.messagehub.messagehubcore.endpoint;

import java.util.concurrent.BlockingQueue;

import com.conradmaz.messagehub.messagehubcore.MessageHubComponent;
import com.conradmaz.messagehub.messagehubcore.message.Message;

/**
 * A {@link MessageEndpoint} is allows for an application to send and receive messages 
 * to a messaging system
 * @author conrad
 *
 */
public interface MessageEndpoint extends MessageHubComponent {
	/**
	 * Initialise a Message Endpoint
	 * 
	 * @param gatewayConfig
	 *            - the MessageEndpoint configuration
	 */
	public void init(MessageEndpointConfig gatewayConfig);

	/**
	 * Send messages to the {@link MessageEndpoint}
	 * 
	 * @param messages
	 *            - the messages to send
	 *      @throws - {@link InterruptedException}
	 */

	public void sendMessages(BlockingQueue<Message> messages) throws InterruptedException;

	/**
	 * Name of the Message Gateway
	 * @return name of the Message Gateway
	 */
	public String getName();
	
	/**
	 * 
	 */
	
	public void consumeMessage(BlockingQueue<Message> messages);

   

}
