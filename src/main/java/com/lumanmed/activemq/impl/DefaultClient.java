/**
 * 
 */
package com.lumanmed.activemq.impl;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.lumanmed.activemq.api.Client;
import com.lumanmed.activemq.api.MessageHandler;
import com.lumanmed.activemq.message.Message;

/**
 * @author Willard
 *
 */
public class DefaultClient implements Client {
	private static final Logger logger = Logger
			.getLogger(DefaultClient.class);
	public static final String CLIENT_PROPERTIES = "client.properties";
	protected String user = "pathinfo";
	protected String password = "pathinfo";
	protected String host = "localhost";
	protected int port = 61616;
	protected long timeout = 10000; // 10 seconds

	protected ActiveMQConnectionFactory factory;
	protected long counter = 0;
	protected PublisherThread publisher;
	protected ListenerThread listener;
	protected Vector<Message> messageSource = new Vector<Message>();
	protected ConcurrentMap<Long, Message> messageResponse = new ConcurrentHashMap<Long, Message>();

	public DefaultClient(String publisherTopic, String listenerTopic) {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader()
					.getResourceAsStream(CLIENT_PROPERTIES));
		} catch (IOException e) {
			logger.error("Fail to load " + CLIENT_PROPERTIES, e);
		}

		factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
		factory.setUserName(user);
		factory.setPassword(password);
		publisher = new PublisherThread(factory, messageSource, publisherTopic);
		listener = new ListenerThread(factory, messageResponse, listenerTopic);
		
		publisher.start();
		
		// wait publisher thread until it goes into WAITING state
		while (true) {
			if (publisher.getState() == State.WAITING) {
				logger.info(String.format("Publisher %d is WAITING for messages.", publisher.getId()));
				break;
			}
		}
		
		listener.start();
	}
	
	protected long counter() {
		synchronized (this) {
			return counter++;
		}
	}

	public Message sendAndWait(Message message) {
		long id = counter();
		message.setId(id);
		messageSource.add(message);
		synchronized (publisher) {
//			logger.debug("State: " + publisher.getState());
			if (publisher.getState() == State.WAITING) {
				logger.info(String.format("Notify publisher %d.", publisher.getId()));
				publisher.notify();
			}
		}
		
		long current = System.currentTimeMillis();
		long limit = current + timeout;
		while (current < limit) {
			Message response = messageResponse.remove(id);
			if (response != null) {
				return response;
			}
			current = System.currentTimeMillis();
		}
		logger.error("sendAndWait timeout on id " + id);
		return null;
	}

	public void waitAndResponse(MessageHandler handler) {
		while (true) {
			if (messageResponse.size() > 0) {
				synchronized (this) {
					for (Long id : messageResponse.keySet()) {
						Message response = handler.handle(messageResponse.remove(id));
						messageSource.add(response);
					}
				}
				synchronized (publisher) {
					if (publisher.getState() == State.WAITING) {
						logger.info(String.format("Notify publisher %d.", publisher.getId()));
						publisher.notify();
					}
				}
			}
		}
	}

}
