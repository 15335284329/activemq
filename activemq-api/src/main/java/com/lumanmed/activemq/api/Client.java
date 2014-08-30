/**
 * 
 */
package com.lumanmed.activemq.api;

/**
 * @author Willard
 *
 */
public interface Client {
	public Response sendAndWait(Request request);
	
	public void waitAndResponse(RequestHandler handler);
}
