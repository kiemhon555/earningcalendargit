package com.euroland.earningcalendar.model.source;

public class SourceConfig {
	
	private Long id;
	private Long sourceId;
	private String cname;
	private PageConfig configText;
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SourceConfig() {
		super();

	}

	public SourceConfig(Long id, Long sourceId, String cname, PageConfig configText) {
		super();
		this.id = id;
		this.sourceId = sourceId;
		this.cname = cname;
		this.configText = configText;
	}
	
}
