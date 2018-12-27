package com.euroland.earningcalendar.model;

import java.util.List;

import com.euroland.earningcalendar.model.date.DateDetails;

public class CrawlingSection {

	private DateDetails dateDetails;
	private List<ElementData> basicDetails;

	public CrawlingSection() {
		super();
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
