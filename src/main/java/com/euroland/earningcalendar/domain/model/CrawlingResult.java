package com.euroland.earningcalendar.domain.model;

import java.util.List;

public class CrawlingResult {

	private int sourceId;
	private List<List<HeaderValue>> results;
	private String method;
	
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public CrawlingResult() {
		super();
	}

	public CrawlingResult(int sourceId, List<List<HeaderValue>> results, String method) {
		super();
		this.sourceId = sourceId;
		this.results = results;
		this.method = method;
	}
	
}
