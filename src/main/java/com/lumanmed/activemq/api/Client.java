/**
 * 
 */
package com.lumanmed.activemq.api;

import com.lumanmed.activemq.message.Message;

/**
 * @author Willard
 *
 */
public interface Client {
	public Message sendAndWait(Message message);
	
	public void waitAndResponse(MessageHandler handler);
}
