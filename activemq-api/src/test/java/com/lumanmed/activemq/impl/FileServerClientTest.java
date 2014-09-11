/**
 * 
 */
package com.lumanmed.activemq.impl;

import junit.framework.TestCase;

import org.junit.Test;

import com.lumanmed.activemq.CameraCaptureTester;

/**
 * @author Willard
 *
 */
public class FileServerClientTest extends TestCase {

	@Test
	public void success() {

	}

	// won't enable this test cause it need camera ready.
	//@Test
	public void testClient() {
		CameraCaptureTester tester = new CameraCaptureTester();
		tester.run();
	}

}
