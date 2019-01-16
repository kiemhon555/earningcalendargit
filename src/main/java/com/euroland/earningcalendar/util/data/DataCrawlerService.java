package com.euroland.earningcalendar.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingSection;
import com.euroland.earningcalendar.model.ElementData;
import com.euroland.earningcalendar.model.HeaderValue;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.model.date.DateDetails;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.matcher.EventMatcherService;

@Service
public class DataCrawlerService {

	@Autowired
	SeleniumService seleniumService;
	
	public static final String ATTRIBUTE_OUT = "attributeOut";
	public static final String ORIGINAL_DATE = "Original Date";
	public static final String ORIGINAL_EVENT = "Original Event";
	public static final String EVENT = "event";

	private static final String REGEX_IDENTIFIER = "regex: ";
	
	private List<List<HeaderValue>> headerValueList = new ArrayList<>();
	
	public void dataLoader(WebDriver driver, PageConfig config) {
		
		config.getCrawlingSections().stream().forEach( c -> {
			
			loadData(driver, c, config.getStandardDate());
		});
		
	}

	public void setCrawledData(List<List<HeaderValue>> hvl) {
		headerValueList = hvl;
	}
	
	public List<List<HeaderValue>> getCrawledData() {
		return headerValueList;
	}
	
	private void loadData(WebDriver driver, CrawlingSection cs, String standardDate) {
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<WebElement> wl = seleniumService.webElementsOut(
				driver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());

		wl.stream().forEach( w -> {
			
			List<HeaderValue> headerValue =  new ArrayList<>();
			
			if(cs.getDateDetails() != null) {
				// index 0 has the original date
				// index 1 has the modified date
				List<String> on = new ArrayList<>();
				on = getDateDetail(w, cs, standardDate);
				
				// Add Header Value for Original Date
				headerValue.add(new HeaderValue(
						ORIGINAL_DATE, 
						on.get(0)));
				System.out.println(ORIGINAL_DATE + " --- " + on.get(0));
				
				// Add Header Value of Modifed Date
				headerValue.add(new HeaderValue(
						cs.getDateDetails().getFullDate().getName(), 
						on.get(1)));
				System.out.println(cs.getDateDetails().getFullDate().getName() + " --- " + on.get(1));

			}
			
			cs.getBasicDetails().stream().forEach( b -> {
	
				String header = b.getName();
				String value = "";
				
				// Get values from the Website
				if(b == cs.getBasicDetails().get(0)) { // Company name must be always on first array
					if(b.getType().equals(ATTRIBUTE_OUT)) {
						value = w.getAttribute(b.getRef());
					} else {
						if (b.getSelectorType().equals("ownText")) {
							value = seleniumService.textOut(w, ".", b.getSelectorType());
						} else {
							value = w.getText();
						}
					}
					value = isSplit(value, b.getSplitter(), b.getPosition());
				} else {
					value = textOrAttribute(w, b);
				}
				
				// if there is a event
				if(header.toLowerCase().equals(EVENT)) {
					// Add Header Value for Original Event Name
					header = ORIGINAL_EVENT;
					headerValue.add(new HeaderValue(header, value));
					System.out.println(header + " --- " + value);
					
					// Add Header Value for Modified Event Name
					header = b.getName();
					value = EventMatcherService.getEvent(value);
				}
				System.out.println(header + " --- " + value);
				// Add Header Value for Basic Details
				headerValue.add(new HeaderValue(header, value));
			});
			
			System.out.println("===============================");
			headerValueList.add(headerValue);
		});
	}
	
	public List<String> getDateDetail(WebElement we, CrawlingSection cs, String standardDate) {

		String date = "";

		String pattern = "";

		DateDetails details = cs.getDateDetails();
		
		// index 0 has the original date
		// index 1 has the modified date
		List<String> on = new ArrayList<>();
		
		if(details.getSplitDate() == null) {
			date = textOrAttribute(we, details.getFullDate());
			pattern = details.getDatePattern();
			
		} else {
			String year = textOrAttribute(we, details.getSplitDate().get(0));
			String month = textOrAttribute(we, details.getSplitDate().get(1));
			String day = textOrAttribute(we, details.getSplitDate().get(2));
			
			date = year + "-" + month + "-" + day;
		}
		
		// adding original date
		on.add(date);
		
		// modifying original date
		date = DateMatcherService.getDate(
				date, pattern, standardDate);
		
		// adding modified date
		on.add(date);
		
		return on;
	}
	
	public String textOrAttribute(WebElement we, ElementData ed) {
		
		String result = "";
		
		if(ed.getType().equals(ATTRIBUTE_OUT)) {
			result = seleniumService.attributeOut(we, ed.getSelector(), ed.getSelectorType(), ed.getRef());
		} else {
			result = seleniumService.textOut(we, ed.getSelector(), ed.getSelectorType());
		}
		
		result = isSplit(result, ed.getSplitter(), ed.getPosition());
		
		return result;
	}
	
	public String isSplit(String text, String splitter, int pos) {
		String result = text;
		
		if(splitter != null) {
			try {
				if(text.contains(splitter)) {
					result = text.split(splitter)[pos];
				} else if (splitter.contains(REGEX_IDENTIFIER)) {
					
					Pattern p = Pattern.compile(splitter.split(REGEX_IDENTIFIER)[1]);
					Matcher m = p.matcher(text);

					if(m.find()) {
					    result = m.group(1);
					}
				}
			} catch (Exception e) {
				result = "";
			}
		} 
		
		return result;
	}
}
