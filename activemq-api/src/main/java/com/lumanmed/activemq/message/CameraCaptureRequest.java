/**
 * 
 */
package com.lumanmed.activemq.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.lumanmed.activemq.api.Strings;

/**
 * @author jwang25
 *
 */
public class CameraCaptureRequest extends BaseMessageAdaptor {
	public static final String TYPE = "1";

	@Override
	public Message toMessage(Session session) throws JMSException {
		Message message = session.createMessage();
		setMessageProperties(message);
		message.setJMSType(TYPE);
		return message;
	}

	@Override
	public void fromMessage(Message message) throws JMSException {
		this.setId(message.getStringProperty(Strings.ID));
	}
}
