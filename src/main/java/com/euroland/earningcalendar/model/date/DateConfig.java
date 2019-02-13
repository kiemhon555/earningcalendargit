package com.euroland.earningcalendar.model.date;

import java.util.List;
import java.util.Map;

public class DateConfig {

	private Map<String, List<String>> monthTranslations;
	private Map<String, String> dateRegexPatterns;
	
	public Map<String, List<String>> getMonthTranslations() {
		return monthTranslations;
	}
	
	public void setMonthTranslations(Map<String, List<String>> monthTranslations) {
		this.monthTranslations = monthTranslations;
	}
	
	public Map<String, String> getDateRegexPatterns() {
		return dateRegexPatterns;
	}
	
	public void setDateRegexPatterns(Map<String, String> dateRegexPatterns) {
		this.dateRegexPatterns = dateRegexPatterns;
	}

	public DateConfig() {
		super();
	}
}
