package com.euroland.earningcalendar.util.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
	
	final static String FIRST_PRIORITY_IDENTIFIER = "(First) ";
	final static String LAST_PRIORITY_IDENTIFIER = "(Last) ";
	final static String EMPTY_STRING = "";
	
	public static TreeMap<String, List<String>> eventList = new TreeMap<>(Collections.reverseOrder());
	public static TreeMap<String, List<String>> firstEventList = new TreeMap<>();
	public static TreeMap<String, List<String>> lastEventList = new TreeMap<>();
	
	public static String getEvent(String event) {

		String result = "";
		
		String trans = event.toLowerCase(Locale.forLanguageTag(LOCALE_CODE));
		
		// Search for the event translation in the config
		List<Entry<String, List<String>>> e = new ArrayList<Map.Entry<String,List<String>>>();
		
		if(firstEventList.size() != 0) {
			e = firstEventList.entrySet().stream().filter( l -> 
				l.getValue().stream().anyMatch(trans::matches)
			).collect(Collectors.toList());
		}
		
		if(eventList.size() != 0 && e.size() == 0) {
			e = eventList.entrySet().stream().filter( l -> 
				l.getValue().stream().anyMatch(trans::matches)
			).collect(Collectors.toList());
		}
		
		if (lastEventList.size() != 0 && e.size() == 0) {
			e = lastEventList.entrySet().stream().filter( l -> 
				l.getValue().stream().anyMatch(trans::matches)
			).collect(Collectors.toList());
		}
		
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
			eventList.putAll(ec.getEventTranslations().entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, 
								l -> l.getValue().stream()
									.map(s -> s = "(?:.*)" + s + "(?:.*)")
									.collect(Collectors.toList())
						))
					);
			
			firstEventList.putAll(eventList.entrySet().stream()
					.filter(l -> l.getKey().contains(FIRST_PRIORITY_IDENTIFIER))
					.collect(Collectors.toMap(
							 k -> k.getKey().replace(FIRST_PRIORITY_IDENTIFIER, EMPTY_STRING), Map.Entry::getValue)));
			eventList.entrySet().removeIf(k -> k.getKey().contains(FIRST_PRIORITY_IDENTIFIER));
			
			lastEventList.putAll(eventList.entrySet().stream()
					.filter(l -> l.getKey().contains(LAST_PRIORITY_IDENTIFIER))
					.collect(Collectors.toMap(
							k -> k.getKey().replace(LAST_PRIORITY_IDENTIFIER, EMPTY_STRING), Map.Entry::getValue)));
			eventList.entrySet().removeIf(k -> k.getKey().contains(LAST_PRIORITY_IDENTIFIER));
			
			status = true;
		}
		return status;
	}
	
}
