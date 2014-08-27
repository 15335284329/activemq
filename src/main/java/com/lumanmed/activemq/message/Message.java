/**
 * 
 */
package com.lumanmed.activemq.message;

import java.io.Serializable;

/**
 * @author Willard
 *
 */
public abstract class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8659295738108190821L;

	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
