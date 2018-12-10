package com.euroland.earningcalendar.model;

import java.util.List;

public class CrawlSection {

	private String selectorType;
	private String selector;
	private List<ElementConf> crawlingElements;

	public String getSelectorType() {
		return selectorType;
	}

	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public List<ElementConf> getCrawlingElements() {
		return crawlingElements;
	}

	public void setCrawlingElements(List<ElementConf> crawlingElements) {
		this.crawlingElements = crawlingElements;
	}

	public CrawlSection() {
		super();

	}

}
