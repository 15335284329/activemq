/**
 * 
 */
package com.lumanmed.activemq;

import java.io.File;
import java.util.Vector;

import com.lumanmed.activemq.api.MessageAdaptor;
import com.lumanmed.activemq.api.MessageHandler;
import com.lumanmed.activemq.message.CameraCaptureResponse;

/**
 * @author Willard
 *
 */
public class CaptureRequestHandler implements MessageHandler {
	private Vector<File> imageQueue;

	public CaptureRequestHandler(Vector<File> imageQueue) {
		this.imageQueue = imageQueue;
	}

	/* (non-Javadoc)
	 * @see com.lumanmed.activemq.api.MessageHandler#handle(com.lumanmed.activemq.api.MessageAdaptor)
	 */
	@Override
	public MessageAdaptor handle(MessageAdaptor request) {
		if (imageQueue.size() == 0) {
			System.out.println("No image in current dir, waiting for images coming in ...");
			while (imageQueue.size() == 0) {
			}
		}
		
		File image = imageQueue.get(0);
		CameraCaptureResponse response = new CameraCaptureResponse();
		response.setId(request.getId());
		response.setImage(image);
		return response;
	}

}
