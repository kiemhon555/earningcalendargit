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
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public class DateMatcherService {
	
	@Autowired
	ConfService confService;
	
	final String DATE_CONFIG_FILE = ".\\src\\main\\resources\\date\\date_conf.json";
	
	public final static String DEFAULT_DATE_FORMAT_1 = "yyyy-MM-dd";
	final static String DEFAULT_DATE_FORMAT_2 = "yy-MM-dd";
	final static String DEFAULT_PATTERN = "pattern-0"; // yyyy=MM-dd
	
	final static String REGEX_DELIMITER = "['-/ ]+";
	final static String DELIMITER = " ";
	
	public final static String NO_MATCH_IDENTIFIER = "No Match";
	
	public static Map<String, List<String>> moList = new HashMap<>();
	public static Map<String, String> patternList = new HashMap<>();
	
	// Return Empty String if failed to Match
	public static String getDate(String date, String pattern, String format) {
		
		String modifiedDate = "";
		
		String dateChanged = NO_MATCH_IDENTIFIER;
		DateTimeFormatter formatter = null;
		
		String d = date.replaceAll(REGEX_DELIMITER, DELIMITER).toLowerCase();

//		if (!d.contains(DELIMITER)) {
//			return modifiedDate;
//		}
		
		if(pattern.equals(""))
			pattern = DEFAULT_PATTERN;
		
		String f = DEFAULT_DATE_FORMAT_1;
	
		Matcher m = Pattern.compile(patternList.get(pattern)).matcher(d);
		if(m.find()) { // if match found
			
			String year = m.group("year");
			String month = m.group("month");
			String day = m.group("day");
			
			if(Integer.parseInt(year) < 100) {
				f = DEFAULT_DATE_FORMAT_2;
			}
			
			day = day.replaceAll("\\D", "");
			
			dateChanged = year + "-" + modify(month) + "-" + modify(day);
	
		} else if (d.contains("kw") || d.contains("week")){
			
			String str = d.replaceAll("[^-?0-9]+", "");
			
			int w = Integer.parseInt(str);
			
			LocalDate desiredDate = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, w).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			
			dateChanged = desiredDate.toString();
		}
		
		if (!dateChanged.equals(NO_MATCH_IDENTIFIER)) {
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
	
	public static String modify(String mod) {
		
		if(isNumeric(mod) > 9) {
			return mod;
		}
		
		String m = NO_MATCH_IDENTIFIER;
		
		List<Entry<String, List<String>>> e = moList.entrySet().stream().filter( l -> 
			l.getValue().contains(mod.toLowerCase())
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
