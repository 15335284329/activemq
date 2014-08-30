/**
 * 
 */
package com.lumanmed.activemq.impl;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.log4j.Logger;


/**
 * @author Willard
 *
 */
public class ListenerThread extends Thread {
	private static final Logger logger = Logger.getLogger(ListenerThread.class);
	private ActiveMQConnectionFactory factory;
	private ConcurrentMap<Long, Message> messageResponse;
	private String topic;

	public ListenerThread(ActiveMQConnectionFactory factory,
			ConcurrentMap<Long, Message> messageResponse, String topic) {
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
				javax.jms.Message message = consumer.receive();
				if (message instanceof ObjectMessage) {
					ObjectMessage objectMessage = (ObjectMessage) message;
					Serializable object = objectMessage.getObject();
					if (object instanceof Message) {
						Message response = (Message) object;
						messageResponse.put(response.getId(), response);
						logger.info(String.format(
								"Message %d received on topic %s.",
								response.getId(), topic));
					} else {
						logger.error("Not a well-formed message "
								+ (object == null ? "null" : object.getClass()));
					}
				} else {
					logger.error("Unexpected message type: "
							+ message.getClass());
				}
			}
		} catch (JMSException e) {
			logger.error("Error occur on receiving message", e);
		}
	}
}
