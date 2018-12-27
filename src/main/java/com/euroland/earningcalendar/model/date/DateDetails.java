package com.euroland.earningcalendar.model.date;

import java.util.List;

import com.euroland.earningcalendar.model.ElementData;

public class DateDetails {
	
	private String datePattern;
	private ElementData fullDate;
	private List<ElementData> splitDate;
	
	public List<ElementData> getSplitDate() {
		return splitDate;
	}
	
	public void setSplitDate(List<ElementData> splitDate) {
		this.splitDate = splitDate;
	}

	public ElementData getFullDate() {
		return fullDate;
	}

	public void setFullDate(ElementData fullDate) {
		this.fullDate = fullDate;
	}

	public DateDetails() {
		super();
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

}
