package com.euroland.earningcalendar.model;

public class Config {
	private int source_id;
	private String cname;
	private String config_text;
	
	public int getSource_id() {
		return source_id;
	}

	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getConfig_text() {
		return config_text;
	}

	public void setConfig_text(String config_text) {
		this.config_text = config_text;
	}

	public Config() {
		super();

	}

	public Config(int source_id, String cname, String config_text) {
		super();
		this.source_id = source_id;
		this.cname = cname;
		this.config_text = config_text;
	}
	
}
