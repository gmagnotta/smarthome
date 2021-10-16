package org.gmagnotta.model;

/**
 * This class represents an OpenHAB server sent event
 */
public class ServerSentEventModel {

	public String topic;
	public String type;
	public String payload;

	public String toString() {
		return "topic : " + topic + ", type : " + type + "; payload = " + payload;
	}
}
