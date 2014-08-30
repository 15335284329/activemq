/**
 * 
 */
package com.lumanmed.activemq.impl;

import org.junit.Test;

import com.lumanmed.activemq.api.Client;
import com.lumanmed.activemq.api.RequestHandler;
import com.lumanmed.activemq.message.CameraControlMessage;
import com.lumanmed.activemq.message.CameraResponseMessage;
import com.lumanmed.activemq.message.Message;

import junit.framework.TestCase;

/**
 * @author Willard
 *
 */
public class DefaultClientTest extends TestCase {
	
	@Test
	public void success() {
		
	}

	// won't enable this test cause it brings up background thread
	//@Test
	public void testClient() {
		CameraClientThread cameraClient = new CameraClientThread();
		cameraClient.start();
		
		PathinfoThread pathinfoThread = new PathinfoThread();
		pathinfoThread.start();
		
		try {
			pathinfoThread.join();
			cameraClient.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static class PathinfoThread extends Thread {
		Client pathinfoClient;
		public void run() {
			pathinfoClient = new DefaultClient("Camera-Request", "Camera-Response");
			
			CameraControlMessage message = new CameraControlMessage();
			System.out.println("Sending ...");
			pathinfoClient.sendAndWait(message);

//			try {
//				sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			System.out.println("Sending again ...");
//			pathinfoClient.sendAndWait(message);
		}
	}
	
	static class CameraClientThread extends Thread {
		Client cameraClient;
		public void run() {
			cameraClient = new DefaultClient("Camera-Response", "Camera-Request");
			cameraClient.waitAndResponse(new MockMessageHandler());
		}
	}
	
	static class MockMessageHandler implements RequestHandler {

		public Message handle(Message message) {
			if (message instanceof CameraControlMessage) {
				System.out.println("Receiving ...");
				Message response = new CameraResponseMessage();
				response.setId(message.getId());
				return response;
			} else {
				System.out.println("Unexpected message type: " + (message == null ? "null" : message.getClass()));
				return null;
			}
		}
		
	}
}
