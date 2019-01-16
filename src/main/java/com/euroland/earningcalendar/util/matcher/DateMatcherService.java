package com.euroland.earningcalendar.util.matcher;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.date.DateConfig;
import com.euroland.earningcalendar.model.date.DatePattern;
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public class DateMatcherService {
	
	@Autowired
	ConfService confService;
	
	final String DATE_CONFIG_FILE = ".\\src\\main\\resources\\date\\date_conf.json";
	
	
	final static String DEFAULT_DATE_FORMAT_1 = "yyyy-MM-dd";
	final static String DEFAULT_DATE_FORMAT_2 = "yy-MM-dd";
	final static String DEFAULT_PATTERN = "pattern-0"; // yyyy=MM-dd
	
	final static String REGEX_DELIMITER = "[,-/ ]+";
	final static String DELIMITER = " ";
	
	public static Map<String, List<String>> moList = new HashMap<>();
	public static Map<String, DatePattern> patternList = new HashMap<>();
	
	// Return Empty String if failed to Match
	public static String getDate(String date, String pattern, String format) {
		
		String modifiedDate = "";
		
		String dateChanged = "No Match";
		DateTimeFormatter formatter = null;
		
		String d = date.replaceAll(REGEX_DELIMITER, DELIMITER).toLowerCase();

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
			
			String day = s[dp.getDay()].replaceAll("\\D", "");
			
			dateChanged = s[dp.getYear()] + "-" + modify(s[dp.getMonth()]) + "-" + modify(day);
	
		} else if (d.contains("kw") || d.contains("week")){
			
			String str = d.replaceAll("[^-?0-9]+", "");
			
			int w = Integer.parseInt(str);
			
			LocalDate desiredDate = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, w).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			
			dateChanged = desiredDate.toString();
		}
		
		if (!dateChanged.equals("No Match")) {
			formatter = DateTimeFormatter.ofPattern(f, Locale.ENGLISH);
			try {
				LocalDate ld = LocalDate.parse(dateChanged, formatter);
				modifiedDate = ld.format(DateTimeFormatter.ofPattern(format));
			} catch (DateTimeParseException e) {
				// modifiedDate will return empty string if parsing has an error
			}
		}
		
		return modifiedDate;
	}

	
	public boolean loadDateConfig(String path) {
		boolean status = false;
		DateConfig dc = (DateConfig) confService.prepareTestConf(path, new DateConfig());
		if(dc != null) {
			patternList = dc.getDateRegexPatterns();
			moList = dc.getMonthTranslations();
			status = true;
		}
		return status;
	}
	
	private static String modify(String mod) {
		
		if(isNumeric(mod) > 9) {
			return mod;
		}
		
		String m = "No Match";
		
		List<Entry<String, List<String>>> e = moList.entrySet().stream().filter( l -> 
			l.getValue().contains(mod)
		).collect(Collectors.toList());
		
		if(e.size() != 0) {
			m = e.get(0).getKey();
		}
		
		return m;
	}
	
	private static int isNumeric(String d)  
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
