package com.lumanmed.microscope;

/**
 * 
 */

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lumanmed.activemq.api.MessageAdaptor;
import com.lumanmed.activemq.api.MessageHandler;
import com.lumanmed.activemq.message.MicroscopeCaptureResponse;

/**
 * @author ye
 * 
 */
public class MicroRequestHandler implements MessageHandler {
    private static final Logger logger = Logger
            .getLogger(MicroRequestHandler.class);
    private Vector<File> imageQueue;

    public MicroRequestHandler(Vector<File> imageQueue) {
        this.imageQueue = imageQueue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lumanmed.activemq.api.MessageHandler#handle(com.lumanmed.activemq
     * .api.MessageAdaptor)
     */
    @Override
    public MessageAdaptor handle(MessageAdaptor request) {
        MicroScope.setupMicroProgram();

        if (imageQueue.size() == 0) {
            System.out
                    .println("No image in current dir, waiting for images coming in ...");
            while (imageQueue.size() == 0) {
            }
        }

        File image = imageQueue.get(0);
        MicroscopeCaptureResponse response = new MicroscopeCaptureResponse();
        response.setId(request.getId());
        response.setImage(image);
        imageQueue.remove(image);
        return response;
    }

}
