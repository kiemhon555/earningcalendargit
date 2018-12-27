package com.euroland.earningcalendar.model;

import java.util.List;

public class PageConfig {

	private String website;
	private String standardDate;
	private String iframe;
	private List<ElementBtn> pagination;
	private List<CrawlingSection> crawlingSections;
	
	public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getIframe() {
		return iframe;
	}
	
	public void setIframe(String iframe) {
		this.iframe = iframe;
	}
	
	public List<ElementBtn> getPagination() {
		return pagination;
	}
	
	public void setPagination(List<ElementBtn> pagination) {
		this.pagination = pagination;
	}

	public PageConfig() {
		super();
	}

	public String getStandardDate() {
		return standardDate;
	}

	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}

	public List<CrawlingSection> getCrawlingSections() {
		return crawlingSections;
	}

	public void setCrawlingSections(List<CrawlingSection> crawlingSections) {
		this.crawlingSections = crawlingSections;
	}
}
