package org.gmagnotta.model;

import java.io.Serializable;

/**
 * This class represent the message that will be understood by Air Conditioner
 */
public class AirConditioningMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String vendor;
	private int model;
	private String power;
	private String mode;
	private String celsius;
	private String temp;
	private String fanSpeed;
	private String swingV;
	private String swingH;
	private String quiet;
	private String turbo;
	private String econo;
	private String light;
	private String filter;
	private String clean;
	private String beep;
	private int sleep;
	
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getCelsius() {
		return celsius;
	}
	public void setCelsius(String celsius) {
		this.celsius = celsius;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getFanSpeed() {
		return fanSpeed;
	}
	public void setFanSpeed(String fanSpeed) {
		this.fanSpeed = fanSpeed;
	}
	public String getSwingV() {
		return swingV;
	}
	public void setSwingV(String swingV) {
		this.swingV = swingV;
	}
	public String getSwingH() {
		return swingH;
	}
	public void setSwingH(String swingH) {
		this.swingH = swingH;
	}
	public String getQuiet() {
		return quiet;
	}
	public void setQuiet(String quiet) {
		this.quiet = quiet;
	}
	public String getTurbo() {
		return turbo;
	}
	public void setTurbo(String turbo) {
		this.turbo = turbo;
	}
	public String getEcono() {
		return econo;
	}
	public void setEcono(String econo) {
		this.econo = econo;
	}
	public String getLight() {
		return light;
	}
	public void setLight(String light) {
		this.light = light;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getClean() {
		return clean;
	}
	public void setClean(String clean) {
		this.clean = clean;
	}
	public String getBeep() {
		return beep;
	}
	public void setBeep(String beep) {
		this.beep = beep;
	}
	public int getSleep() {
		return sleep;
	}
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	
}
