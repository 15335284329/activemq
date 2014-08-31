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
import com.lumanmed.activemq.api.MessageAdaptor;
import com.lumanmed.activemq.api.MessageHandler;

/**
 * @author Willard
 *
 */
public class DefaultClient implements Client {
	private static final Logger logger = Logger.getLogger(DefaultClient.class);
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
	protected Vector<MessageAdaptor> messageSource = new Vector<MessageAdaptor>();
	protected ConcurrentMap<String, MessageAdaptor> messageResponse = new ConcurrentHashMap<String, MessageAdaptor>();
	

	public DefaultClient(String publisherTopic, String listenerTopic) {
		this(publisherTopic, listenerTopic, null);
	}

	public DefaultClient(String publisherTopic, String listenerTopic, String parameter) {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader()
					.getResourceAsStream(CLIENT_PROPERTIES));
		} catch (IOException e) {
			logger.error("Fail to load " + CLIENT_PROPERTIES, e);
		}

		factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port + (parameter == null ? "" : "?" + parameter));
		factory.setUserName(user);
		factory.setPassword(password);
		publisher = new PublisherThread(factory, messageSource, publisherTopic);
		listener = new ListenerThread(factory, messageResponse, listenerTopic);

		publisher.start();

		// wait publisher thread until it goes into WAITING state
		while (true) {
			if (publisher.getState() == State.WAITING) {
				logger.info(String.format(
						"Publisher %d is WAITING for messages.",
						publisher.getId()));
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

	public MessageAdaptor sendAndWait(MessageAdaptor request) {
		String id = String.valueOf(counter());
		request.setId(id);
		messageSource.add(request);
		synchronized (publisher) {
			// logger.debug("State: " + publisher.getState());
			if (publisher.getState() == State.WAITING) {
				logger.info(String.format("Notify publisher %d.",
						publisher.getId()));
				publisher.notify();
			}
		}

		long current = System.currentTimeMillis();
		long limit = current + timeout;
		while (current < limit) {
			MessageAdaptor response = messageResponse.remove(id);
			if (response != null) {
				return response;
			}
			current = System.currentTimeMillis();
		}
//		logger.debug(""+messageResponse+" "+messageResponse.size());
		logger.error("sendAndWait timeout on id " + id);
		return null;
	}

	public void waitAndResponse(MessageHandler handler) {
		while (true) {
			if (messageResponse.size() > 0) {
				synchronized (this) {
					for (String id : messageResponse.keySet()) {
						MessageAdaptor response = handler
								.handle(messageResponse.remove(id));
						messageSource.add(response);
					}
				}
				synchronized (publisher) {
					if (publisher.getState() == State.WAITING) {
						logger.info(String.format("Notify publisher %d.",
								publisher.getId()));
						publisher.notify();
					}
				}
			}
		}
	}

}
