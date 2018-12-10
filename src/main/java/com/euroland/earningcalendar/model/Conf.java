package com.euroland.earningcalendar.model;

import java.util.List;

public class Conf {

	private String dateFormat;
	private ElementConf preSection;
	private List<CrawlSection> crawlingSection;

	public Conf() {
		super();

	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public ElementConf getPreSection() {
		return preSection;
	}

	public void setPreSection(ElementConf preSection) {
		this.preSection = preSection;
	}

	public List<CrawlSection> getCrawlingSection() {
		return crawlingSection;
	}

	public void setCrawlingSection(List<CrawlSection> crawlingSection) {
		this.crawlingSection = crawlingSection;
	}

}
