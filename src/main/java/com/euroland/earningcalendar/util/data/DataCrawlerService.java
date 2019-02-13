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
	public static final String TXT_OUT = "txtOut";
	public static final String URL_OUT = "urlOut";
	public static final String ORIGINAL = "Original ";
	public static final String EVENT = "Event";

	private static final String REGEX_IDENTIFIER = "regex: ";

	private static final String CONCAT_IDENTIFIER = " concat: ";

	private static final String SELECTOR_IDENTIFIER = "([\\:\\/\\\">\\[\\]])";

	private WebDriver webDriver = null;
	
	private List<List<HeaderValue>> headerValueList = new ArrayList<>();
	
	public void dataLoader(WebDriver driver, PageConfig config) {
		
		webDriver = driver;
		
		config.getCrawlingSections().stream().forEach( c -> {
			
			loadData(c, config.getStandardDate());
		});
		
	}

	public void setCrawledData(List<List<HeaderValue>> hvl) {
		if(hvl == null) {
			headerValueList = new ArrayList<>();
		} else {
			headerValueList.addAll(hvl);
		}
	}
	
	public List<List<HeaderValue>> getCrawledData() {
		return headerValueList;
	}
	
	private void loadData(CrawlingSection cs, String standardDate) {
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		List<WebElement> wl = seleniumService.webElementsOut(
				webDriver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());

		wl.stream().forEach( w -> {
			
			List<HeaderValue> headerValue =  new ArrayList<>();
			
			if(cs.getDateDetails() != null) {
				// index 0 has the original date
				// index 1 has the modified date
				List<String> on = new ArrayList<>();
				on = getDateDetail(w, cs, standardDate);
				
				String header = cs.getDateDetails().getFullDate().getName();
				
				// Add Header Value for Original Date
				headerValue.add(new HeaderValue(
						ORIGINAL + header, 
						on.get(0)));
				System.out.println(ORIGINAL + header + " --- " + on.get(0));
				
				// Add Header Value of Modifed Date
				headerValue.add(new HeaderValue(
						header, 
						on.get(1)));
				System.out.println(header + " --- " + on.get(1));

			}
			
			headerValue.addAll(loadBasicDetails(cs.getBasicDetails(), w));
			
			System.out.println("===============================");
			headerValueList.add(headerValue);
		});
		
		System.out.println("unprocessed data: " + headerValueList.size());
	}
	
	public List<HeaderValue> loadBasicDetails(List<ElementData> listBasicDetails, WebElement w) {
		List<HeaderValue> headerValue =  new ArrayList<>();
		
		listBasicDetails.stream().forEach( b -> {
			
			String header = b.getName();
			String value = "";
			
			// Get values from the Website
			if(b == listBasicDetails.get(0)) { // Company name must be always on first array
				if(b.getType().contains(ATTRIBUTE_OUT)) {
					value = w.getAttribute(b.getRef());
				}
				
				if(value.equals("") && b.getType().contains(TXT_OUT)){
					if (b.getSelectorType().equals("ownText")) {
						value = seleniumService.textOut(w, ".", b.getSelectorType());
					} else {
						value = w.getText();
					}
				}
				
				value = isSplit(value, b.getSplitter(), b.getPosition());
			} else {
				value = urltextOrAttribute(w, b);
			}
			
			if(header.toLowerCase().equals("symbol")) {
				value = value.replaceAll("%20", " ");
			}
			
			// if there is a event
			if(header.toLowerCase().equals(EVENT.toLowerCase())) {
				// Add Header Value for Original Event Name
				header = ORIGINAL + EVENT;
				headerValue.add(new HeaderValue(header, value));
				System.out.println(header + " --- " + value);
				
				// Add Header Value for Modified Event Name
				header = EVENT;
				value = EventMatcherService.getEvent(value);
			}
			System.out.println(header + " --- " + value);
			// Add Header Value for Basic Details
			headerValue.add(new HeaderValue(header, value));
		});
		
		return headerValue;
	}
	
	public List<String> getDateDetail(WebElement we, CrawlingSection cs, String standardDate) {

		String date = "";

		String pattern = "";

		DateDetails details = cs.getDateDetails();
		
		// index 0 has the original date
		// index 1 has the modified date
		List<String> on = new ArrayList<>();
		
		if(details.getSplitDate() == null) {
			date = urltextOrAttribute(we, details.getFullDate());
			pattern = details.getDatePattern();
			
		} else {
			String year = urltextOrAttribute(we, details.getSplitDate().get(0));
			String month = urltextOrAttribute(we, details.getSplitDate().get(1));
			String day = urltextOrAttribute(we, details.getSplitDate().get(2));
			
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
	
	public String urltextOrAttribute(WebElement we, ElementData ed) {
		
		String result = "";
		
		if(ed.getSelectorType() == null && (ed.getType() == null || !ed.getType().equals(URL_OUT))) {
			return ed.getSelector();
		}
		
		if(ed.getType().equals(URL_OUT)) {
			result = webDriver.getCurrentUrl();
		}
		
		if(ed.getType().contains(ATTRIBUTE_OUT)) {
			result = seleniumService.attributeOut(we, ed.getSelector(), ed.getSelectorType(), ed.getRef());
		}
		
		if (result.equals("") && ed.getType().contains(TXT_OUT)){

			if(ed.getSelector().contains(CONCAT_IDENTIFIER)) {
				String[] selectors = ed.getSelector().split(CONCAT_IDENTIFIER);
				for(String s : selectors) {
					result = result + " " +textOrSelector(we, s, ed.getSelectorType()).trim();
				}
			} else {
				result = seleniumService.textOut(we, ed.getSelector(), ed.getSelectorType());
			}
		}
		
		result = isSplit(result.trim(), ed.getSplitter(), ed.getPosition());
		
		return result;
	}
	
	private String textOrSelector(WebElement we, String selector, String selectorType) {
		
		String result = "";
		
		Pattern pattern = Pattern.compile(SELECTOR_IDENTIFIER);
		Matcher matcher = pattern.matcher(selector);
		
		if(!matcher.find()) {
			result = result + " " + selector;
		} else {
			result = result + " " + seleniumService.textOut(we, selector, selectorType);
		}
		
		return result;
	}
	
	public String isSplit(String text, String splitter, int pos) {
		String result = "";

		result = text.replaceAll("\n", " ");
		
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
		
		return result.trim();
	}
}
