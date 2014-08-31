/**
 * 
 */
package com.lumanmed.activemq.impl;

import java.util.Vector;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.log4j.Logger;

import com.lumanmed.activemq.api.MessageAdaptor;


/**
 * @author Willard
 *
 */
public class PublisherThread extends Thread {
	private static final Logger logger = Logger
			.getLogger(PublisherThread.class);
	private ActiveMQConnectionFactory factory;
	private Vector<MessageAdaptor> messageSource;
	private String topic;

	public PublisherThread(ActiveMQConnectionFactory factory,
			Vector<MessageAdaptor> messageSource, String topic) {
		this.factory = factory;
		this.messageSource = messageSource;
		this.topic = topic;
	}

	@Override
	public void run() {
		logger.info("Publisher thread started.");
		while (true) {
			// Assume that outside PublisherThread messageSource's size can only
			// be increased. No remove actions outside PublisherThread.
			if (messageSource.size() > 0) {
				// The first element is valid since messageSource only increase
				// never decrease outside PublisherThread.
				MessageAdaptor request = messageSource.remove(0);

				try {
					logger.info(String.format("Start connection %s ...",
							request.getId()));
					Connection connection = factory.createConnection();
					connection.start();
					Session session = connection.createSession(false,
							Session.AUTO_ACKNOWLEDGE);
					Destination dest = new ActiveMQTopic(topic);
					MessageProducer producer = session.createProducer(dest);
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

					Message message = request.toMessage(session);
					logger.info(String.format("Send message %s on topic %s...",
							request.getId(), topic));
					producer.send(message);
					connection.close();
				} catch (JMSException e) {
					logger.error(String.format(
							"Error occur on sending message %s on topic %s.",
							request.getId(), topic), e);
				}
			} else {
				logger.info("No messages to publish. Go to sleep and wait for notify.");
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException e) {
					logger.error("Waiting for notify interrupted.", e);
				}
			}
		}
	}
}
