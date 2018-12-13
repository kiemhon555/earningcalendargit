package com.euroland.earningcalendar.util.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.DateConfig;
import com.euroland.earningcalendar.model.DatePattern;
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public class DateMatcher {
	
	@Autowired
	ConfService confService;
	
	final String DATE_CONFIG_FILE = ".\\src\\main\\resources\\date\\date_conf.json";
	
	
	final String DEFAULT_DATE_FORMAT_1 = "yyyy-MM-dd";
	final String DEFAULT_DATE_FORMAT_2 = "yy-MM-dd";
	final String DEFAULT_PATTERN = "pattern-0"; // yyyy=MM-dd
	
	final String REGEX_DELIMITER = "[,-/ ]+";
	final String DELIMITER = " ";
	
	private Map<String, List<String>> moList = new HashMap<>();
	private Map<String, DatePattern> patternList = new HashMap<>();
	
	// Return Empty String if failed to Match
	public String getDate(String date, String pattern, String format) {
		
		String modifiedDate = "";
		

		loadDateConfig();
		
		String dateChanged = "No Match";
		DateTimeFormatter formatter = null;
		
		String d = date.replaceAll(REGEX_DELIMITER, DELIMITER);
		if (!d.contains(DELIMITER)) {
			return modifiedDate;
		}
		
		if(pattern.equals(""))
			pattern = DEFAULT_PATTERN;
		
		DatePattern dp = patternList.get(pattern);
		String f = DEFAULT_DATE_FORMAT_1;
	
		Matcher m = Pattern.compile(dp.getRegex()).matcher(d);
		if(m.find()) { // if match found
			String[] s = m.group().split(DELIMITER);
			if(Integer.parseInt(s[dp.getYear()]) < 100) {
				f = DEFAULT_DATE_FORMAT_2;
			}
			dateChanged = s[dp.getYear()] + "-" + modify(s[dp.getMonth()]) + "-" + modify(s[dp.getDay()]);
	
		}
		
		if (!dateChanged.equals("No Match")) {
			formatter = DateTimeFormatter.ofPattern(f, Locale.ENGLISH);
			modifiedDate = LocalDate.parse(dateChanged, formatter).format(DateTimeFormatter.ofPattern(format));
		}
		
//		System.out.println(modifiedDate);
		
		return modifiedDate;
	}

	
	private void loadDateConfig() {
		DateConfig dc = (DateConfig) confService.prepareTestConf(DATE_CONFIG_FILE, new DateConfig());
		patternList = dc.getDateRegexPatterns();
		moList = dc.getMonthTranslations();
	}
	
	private String modify(String mod) {
		
		if(isNumeric(mod) > 9) {
			return mod;
		}
		
		String m = "No Match";
		
		for(Map.Entry<String, List<String>> l : moList.entrySet()) {
			int mo = l.getValue().indexOf(mod.toLowerCase());
			if (mo != -1) {
				m = l.getKey();
				break;
			}
		}
		
		return m;
	}
	
	private int isNumeric(String d)  
	{  
		int i = 0;
	  try  
	  {  
	    i = Integer.parseInt(d);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return 0;  // not integer
	  }  
	  return i;  
	}
}
