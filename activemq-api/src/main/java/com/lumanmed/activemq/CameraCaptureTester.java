/**
 * 
 */
package com.lumanmed.activemq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lumanmed.activemq.api.Client;
import com.lumanmed.activemq.api.MessageAdaptor;
import com.lumanmed.activemq.impl.FileServerClient;
import com.lumanmed.activemq.message.CameraCaptureRequest;
import com.lumanmed.activemq.message.CameraCaptureResponse;

/**
 * @author Willard
 *
 */
public class CameraCaptureTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CameraCaptureTester tester = new CameraCaptureTester();
		tester.run();
	}
	
	public void run() {
		Client pathinfoClient = new FileServerClient("Camera-Request",
				"Camera-Response");

		CameraCaptureRequest message = new CameraCaptureRequest();
		System.out.println("Sending ...");
		MessageAdaptor response = pathinfoClient.sendAndWait(message);

		if (response instanceof CameraCaptureResponse) {
			CameraCaptureResponse cameraCaptureResponse = (CameraCaptureResponse) response;
			BufferedInputStream bin = new BufferedInputStream(
					cameraCaptureResponse.getInputStream());

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			File file = new File(sdf.format(date) + ".jpg");
			try {
				FileOutputStream fos = new FileOutputStream(file);
				byte[] block = new byte[4096];
				while (true) {
					int readLength = bin.read(block);
					if (readLength == -1)
						break;// end of file
					fos.write(block, 0, readLength);
				}
				bin.close();
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Unkown response message " + response);
		}
	}

}
