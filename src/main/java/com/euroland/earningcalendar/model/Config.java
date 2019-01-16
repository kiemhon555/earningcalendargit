package com.euroland.earningcalendar.model;

public class Config {
	
	private int id;
	private int source_id;
	private String cname;
	private PageConfig config_text;
	
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

	public PageConfig getConfig_text() {
		return config_text;
	}

	public void setConfig_text(PageConfig config_text) {
		this.config_text = config_text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Config() {
		super();

	}

	public Config(int id, int source_id, String cname, PageConfig config_text) {
		super();
		this.id = id;
		this.source_id = source_id;
		this.cname = cname;
		this.config_text = config_text;
	}
	
}
