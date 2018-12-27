package com.euroland.earningcalendar.model.event;

import java.util.List;
import java.util.Map;

public class EventConfig {
	
	private Map<String, List<String>> eventTranslations;

	public Map<String, List<String>> getEventTranslations() {
		return eventTranslations;
	}

	public void setEventTranslations(Map<String, List<String>> eventTranslations) {
		this.eventTranslations = eventTranslations;
	}

	public EventConfig() {
		super();
	}
	
}
