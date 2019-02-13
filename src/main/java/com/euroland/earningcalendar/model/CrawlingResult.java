package com.euroland.earningcalendar.model;

import java.util.List;

public class CrawlingResult {
	
	private int sourceId;
	private List<List<HeaderValue>> results;
	
	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
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

	public CrawlingResult(int sourceId, List<List<HeaderValue>> results) {
		super();
		this.sourceId = sourceId;
		this.results = results;
	}
	
}
