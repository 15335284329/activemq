/**
 * 
 */
package com.lumanmed.activemq.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;

import com.lumanmed.activemq.api.Request;
import com.lumanmed.activemq.api.Strings;

/**
 * @author jwang25
 *
 */
public abstract class BaseRequest implements Request {
	protected Map<String, String> properties = new HashMap<String, String>();

	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setPropertiy(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public void setId(String id) {
		properties.put(Strings.ID, id);
	}

	@Override
	public String getId() {
		String id = properties.get(Strings.ID);
		return id == null ? "" : id;
	}
	
	protected void setMessageProperties(Message message) throws JMSException {
		for (Entry<String, String> entry : properties.entrySet()) {
			if (!Strings.ID.equals(entry.getKey())) {
				message.setStringProperty(entry.getKey(), entry.getValue());
			}
		}
		message.setJMSMessageID(getId());
	}
}
