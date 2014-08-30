/**
 * 
 */
package com.lumanmed.activemq.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author jwang25
 *
 */
public class CameraCaptureRequest extends BaseRequest {

	@Override
	public Message toMessage(Session session) throws JMSException {
		Message message = session.createMessage();
		setMessageProperties(message);
		return message;
	}
}
