package com.euroland.earningcalendar.model;

import java.util.List;

public class HeaderValueData {
	
	private int source_id;
	private List<List<HeaderValue>> header_value;
	
	public int getSource_id() {
		return source_id;
	}
	
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}
	
	public List<List<HeaderValue>> getHeader_value() {
		return header_value;
	}
	
	public void setHeader_value(List<List<HeaderValue>> header_value) {
		this.header_value = header_value;
	}
	
	public HeaderValueData(int source_id, List<List<HeaderValue>> header_value) {
		super();
		this.source_id = source_id;
		this.header_value = header_value;
	}
	
	public HeaderValueData() {
		super();

	}
}
