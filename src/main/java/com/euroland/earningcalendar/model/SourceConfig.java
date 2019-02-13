package com.euroland.earningcalendar.model;

public class SourceConfig {
	
	private int id;
	private int sourceId;
	private String cname;
	private PageConfig configText;
	
	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public PageConfig getConfigText() {
		return configText;
	}

	public void setConfigText(PageConfig configText) {
		this.configText = configText;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SourceConfig() {
		super();

	}

	public SourceConfig(int id, int sourceId, String cname, PageConfig configText) {
		super();
		this.id = id;
		this.sourceId = sourceId;
		this.cname = cname;
		this.configText = configText;
	}
	
}
