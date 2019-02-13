package com.euroland.earningcalendar.model.data;

public class HeaderValue {
	private String header;
	private String value;
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public HeaderValue(String header, String value) {
		super();
		this.header = header;
		this.value = value;
	}
	
	public HeaderValue() {
		super();
	}
}
