/**
 * 
 */
package com.lumanmed.activemq.api;

/**
 * @author Willard
 *
 */
public interface Client {
	public MessageAdaptor sendAndWait(MessageAdaptor request);
	
	public void waitAndResponse(MessageHandler handler);
}
