package com.euroland.earningcalendar.domain.model;

import java.util.List;

public class CrawlingResult {

	private Long sourceId;
	private List<List<HeaderValue>> jsonData;
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public List<List<HeaderValue>> getJsonData() {
		return jsonData;
	}
	
	public void setJsonData(List<List<HeaderValue>> jsonData) {
		this.jsonData = jsonData;
	}
	
	public CrawlingResult() {
		super();
	}

	public CrawlingResult(Long sourceId, List<List<HeaderValue>> jsonData) {
		super();
		this.sourceId = sourceId;
		this.jsonData = jsonData;
	}
	
}
