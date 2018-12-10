package com.euroland.earningcalendar.util.date;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.DatePattern;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class DateMatcher {
	
	final String REGEX_CONFIG_FILE = ".\\src\\main\\resources\\date\\regex_conf.json";
	final String MONTH_CONFIG_FILE = ".\\src\\main\\resources\\date\\month_conf.json";
	
	final String REGEX_DELIMITER = "[,-/ ]+";
	final String DELIMITER = " ";
	
	Map<String, List<String>> moList = new HashMap<>();
	Map<String, DatePattern> patternList = new HashMap<>();
	
	// Return Empty String if failed to Match
	public String getDate(String date, String format) {
		
		String modifiedDate = "";
		
		try {
			loadRegex();
			loadMonth();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String dateChanged = "No Match";
		String d = date.replaceAll(REGEX_DELIMITER, DELIMITER);
		if (!d.contains(DELIMITER)) {
			return modifiedDate;
		}
		System.out.println(date);
		
		for(Map.Entry<String, DatePattern> l : patternList.entrySet()) {
			
			DatePattern dp = l.getValue();
			Matcher m = Pattern.compile(dp.getRegex()).matcher(d);
			if(m.find()) { // if match found
				String[] s = m.group().split(DELIMITER);
				dateChanged = s[dp.getYear()] + "-" + modify(s[dp.getMonth()]) + "-" + modify(s[dp.getDay()]);
				break;
			}
		}
		
		if (!dateChanged.equals("No Match")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
			modifiedDate = LocalDate.parse(dateChanged, formatter).format(DateTimeFormatter.ofPattern(format));
		}
		
		return modifiedDate;
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
	
	private void loadRegex() throws IOException {
		
		System.out.append(REGEX_CONFIG_FILE);
		Gson gson = new Gson();
		String json = getStrFromFile(REGEX_CONFIG_FILE);
		
		patternList = gson.fromJson(json, new TypeToken<Map<String, DatePattern>>() {}.getType());
	}

	private void loadMonth() throws IOException {
		
		System.out.append(MONTH_CONFIG_FILE);
		Gson gson = new Gson();
		String json = getStrFromFile(MONTH_CONFIG_FILE);
		
		moList = gson.fromJson(json, new TypeToken<Map<String, List<String>>>() {}.getType());

	}
	
	private String getStrFromFile(String pathname) throws IOException {
		FileInputStream fis = new FileInputStream(pathname);
		StringBuilder sb = new StringBuilder();
		Reader r = new InputStreamReader(fis, "UTF-8");
		char[] buf = new char[1024];
		int amt = r.read(buf);
		while (amt > 0) {
			sb.append(buf, 0, amt);
			amt = r.read(buf);
		}
		fis.close();
		return sb.toString();
	}
}
