package com.euroland.earningcalendar.util.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.event.EventConfig;
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public class EventMatcherService {

	@Autowired
	ConfService confService;
	
	final String EVENT_CONFIG_FILE = ".\\src\\main\\resources\\event\\event_conf.json";
	final static String LOCALE_CODE = "utf-8";
	
	public static Map<String, List<String>> eventList = new HashMap<>();
	
	public static String getEvent(String event) {

		String result = "";
		
		String trans = event.toLowerCase(Locale.forLanguageTag(LOCALE_CODE));
		
		// Search for the event translation in the config
		List<Entry<String, List<String>>> e = eventList.entrySet().stream().filter( l -> 
			l.getValue().stream().anyMatch(trans::contains)
		).collect(Collectors.toList());
		
		if(e.size() != 0) {
			// If there is a match
			result = e.get(0).getKey();
		}
		
		return result;
	}
	
	public boolean loadEventConfig(String path) {
		boolean status = false;
		
		EventConfig ec = (EventConfig) confService.prepareTestConf(path, new EventConfig());
		if(ec != null) {
			eventList = new TreeMap<String, List<String>>(ec.getEventTranslations()).descendingMap();
			status = true;
		}
		return status;
	}
	
}
