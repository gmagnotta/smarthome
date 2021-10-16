package org.gmagnotta.model;

import java.io.Serializable;

/**
 * This class represents a Rest Response from OpenHab
 */
public class OpenHABRestItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String link;
	private String state;
	private String label;
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
