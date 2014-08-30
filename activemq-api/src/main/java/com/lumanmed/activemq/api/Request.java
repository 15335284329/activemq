/**
 * 
 */
package com.lumanmed.activemq.api;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author jwang25
 *
 */
public interface Request {
	public String getProperty(String key);

	public void setId(String id);
	
	public String getId();
	
	public Message toMessage(Session session) throws JMSException;
}
