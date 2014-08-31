/**
 * 
 */
package com.lumanmed.activemq.message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.log4j.Logger;

/**
 * @author Willard
 *
 */
public class CameraCaptureResponse extends BaseMessageAdaptor {
	private static final Logger logger = Logger
			.getLogger(CameraCaptureResponse.class);
	public static final String TYPE = "2";
	private File image;
	private InputStream inputStream;

	public CameraCaptureResponse() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lumanmed.activemq.api.MessageAdaptor#toMessage(javax.jms.Session)
	 */
	@Override
	public Message toMessage(Session session) throws JMSException {
		BlobMessage message = ((ActiveMQSession) session)
				.createBlobMessage(image);
		setMessageProperties(message);
		message.setJMSType(TYPE);
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lumanmed.activemq.api.MessageAdaptor#fromMessage(javax.jms.Message)
	 */
	@Override
	public void fromMessage(Message message) throws JMSException {
		try {
			inputStream = ((BlobMessage) message).getInputStream();
		} catch (IOException e) {
			logger.error("Can't get input stream.", e);
		}

	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
