/**
 * 
 */
package com.lumanmed.activemq.impl;

import java.util.concurrent.ConcurrentMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.log4j.Logger;

import com.lumanmed.activemq.api.MessageAdaptor;
import com.lumanmed.activemq.api.Strings;
import com.lumanmed.activemq.message.MessageFactory;

/**
 * @author Willard
 *
 */
public class ListenerThread extends Thread {
	private static final Logger logger = Logger.getLogger(ListenerThread.class);
	private ActiveMQConnectionFactory factory;
	private ConcurrentMap<String, MessageAdaptor> messageResponse;
	private String topic;

	public ListenerThread(ActiveMQConnectionFactory factory,
			ConcurrentMap<String, MessageAdaptor> messageResponse, String topic) {
		this.factory = factory;
		this.messageResponse = messageResponse;
		this.topic = topic;
	}

	@Override
	public void run() {
		logger.info("Listener thread started.");
		try {
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = new ActiveMQTopic(topic);

			MessageConsumer consumer = session.createConsumer(dest);

			while (true) {
				Message message = consumer.receive();

				String id = message.getStringProperty(Strings.ID);
				if (id == null) {
					logger.error("Property ID is null. Message is " + message);
				} else {
					messageResponse
							.put(id, MessageFactory.wrapMessage(message));
					logger.info(String.format(
							"Message %s received on topic %s.",
							message.getStringProperty(Strings.ID), topic));
//					logger.debug(""+messageResponse+" "+messageResponse.size());
				}

			}
		} catch (JMSException e) {
			logger.error("Error occur on receiving message", e);
		}
	}
}
