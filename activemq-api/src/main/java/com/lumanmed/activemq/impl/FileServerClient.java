/**
 * 
 */
package com.lumanmed.activemq.impl;

/**
 * @author Willard
 *
 */
public class FileServerClient extends DefaultClient {

	public FileServerClient(String publisherTopic, String listenerTopic) {
		// TODO replace localhost
		super(publisherTopic, listenerTopic, "jms.blobTransferPolicy.defaultUploadUrl=http://localhost:8161/fileserver/");
	}

}
