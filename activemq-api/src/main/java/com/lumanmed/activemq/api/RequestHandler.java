/**
 * 
 */
package com.lumanmed.activemq.api;

/**
 * @author Willard
 *
 */
public interface RequestHandler {
	public Response handle(Request request);
}
