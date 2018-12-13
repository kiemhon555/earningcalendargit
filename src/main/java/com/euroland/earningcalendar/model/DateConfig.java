package com.euroland.earningcalendar.model;

import java.util.List;
import java.util.Map;

public class DateConfig {

	private Map<String, List<String>> monthTranslations;
	private Map<String, DatePattern> dateRegexPatterns;
	
	public Map<String, List<String>> getMonthTranslations() {
		return monthTranslations;
	}
	
	public void setMonthTranslations(Map<String, List<String>> monthTranslations) {
		this.monthTranslations = monthTranslations;
	}
	
	public Map<String, DatePattern> getDateRegexPatterns() {
		return dateRegexPatterns;
	}
	
	public void setDateRegexPatterns(Map<String, DatePattern> dateRegexPatterns) {
		this.dateRegexPatterns = dateRegexPatterns;
	}

	public DateConfig() {
		super();
	}
}
