package com.euroland.earningcalendar.domain.model;

import java.util.List;

public class CrawlingResult {

	private Long sourceId;
	private List<List<HeaderValue>> results;
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public List<List<HeaderValue>> getResults() {
		return results;
	}
	
	public void setResults(List<List<HeaderValue>> results) {
		this.results = results;
	}
	
	public CrawlingResult() {
		super();
	}

	public CrawlingResult(Long sourceId, List<List<HeaderValue>> results) {
		super();
		this.sourceId = sourceId;
		this.results = results;
	}
	
}
