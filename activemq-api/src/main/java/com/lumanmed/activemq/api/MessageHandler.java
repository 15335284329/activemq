/**
 * 
 */
package com.lumanmed.activemq.api;

import com.lumanmed.activemq.message.Message;

/**
 * @author Willard
 *
 */
public interface MessageHandler {
	public Message handle(Message message);
}
