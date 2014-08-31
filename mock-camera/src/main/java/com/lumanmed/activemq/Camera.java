package com.lumanmed.activemq;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import com.lumanmed.activemq.api.Client;
import com.lumanmed.activemq.impl.FileServerClient;

/**
 * Hello world!
 *
 */
public class Camera {
	private Client client;
	private CaptureRequestHandler handler;
	private Vector<File> imageQueue;
	
	public Camera() {
		imageQueue = new Vector<File>();
		DriverThread driver = new DriverThread(imageQueue);
		driver.start();
		
		client = new FileServerClient("Camera-Response", "Camera-Request");
		handler = new CaptureRequestHandler(imageQueue);
		client.waitAndResponse(handler);
		System.out.println("Camera started. Waiting for request ...");
	}
	
	private static class DriverThread extends Thread {
		private Vector<File> imageQueue;
		public DriverThread(Vector<File> imageQueue) {
			this.imageQueue = imageQueue;
		}

		@Override
		public void run() {
			File currentDir = new File(".");
			
			while (true) {
				File[] images = currentDir.listFiles(new FileFilter(){

					@Override
					public boolean accept(File file) {
						if (file.isFile() && file.getName().endsWith(".jpg")) {
							return true;
						}
						return false;
					}});
				for (File i : images) {
					boolean found = false;
					for (File j : imageQueue) {
						if (i.getAbsolutePath().equals(j.getAbsolutePath())) {
							found = true;
						}
					}
					
					if (!found) {
						System.out.println(String.format("Found image %s, added to queue.", i.getAbsolutePath()));
						imageQueue.add(i);
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Camera();
	}
}
