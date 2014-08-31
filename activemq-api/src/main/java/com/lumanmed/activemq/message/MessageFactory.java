/**
 * 
 */
package com.lumanmed.activemq.message;

import javax.jms.JMSException;
import javax.jms.Message;

import com.lumanmed.activemq.api.MessageAdaptor;

/**
 * @author Willard
 *
 */
public class MessageFactory {
	public static MessageAdaptor wrapMessage(Message message) throws JMSException {
		MessageAdaptor adaptor = null;
		if (CameraCaptureRequest.TYPE.equals(message.getJMSType())) {
			adaptor = new CameraCaptureRequest();
		} else if (CameraCaptureResponse.TYPE.equals(message.getJMSType())) {
			adaptor = new CameraCaptureResponse();
		} else {
			throw new JMSException(String.format("Unknown JMS type: %s", message.getJMSType()));
		}
		
		adaptor.fromMessage(message);
		return adaptor;
	}
}
