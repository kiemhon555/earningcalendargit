package com.euroland.earningcalendar.model.source;

import java.util.List;

public class CrawlingSection {

	private ElementData modifyElement;
	private DateDetails dateDetails;
	private List<ElementData> basicDetails;

	public CrawlingSection() {
		super();
	}

	public ElementData getModifyElement() {
		return modifyElement;
	}

	public void setModifyElement(ElementData modifyElement) {
		this.modifyElement = modifyElement;
	}
	
	public DateDetails getDateDetails() {
		return dateDetails;
	}

	public void setDateDetails(DateDetails dateDetails) {
		this.dateDetails = dateDetails;
	}

	public List<ElementData> getBasicDetails() {
		return basicDetails;
	}

	public void setBasicDetails(List<ElementData> basicDetails) {
		this.basicDetails = basicDetails;
	}
}
