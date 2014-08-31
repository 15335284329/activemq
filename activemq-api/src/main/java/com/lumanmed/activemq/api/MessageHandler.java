/**
 * 
 */
package com.lumanmed.activemq.api;

/**
 * @author Willard
 *
 */
public interface MessageHandler {
	public MessageAdaptor handle(MessageAdaptor request);
}
