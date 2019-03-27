package com.euroland.earningcalendar.util.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.HeaderValue;
import com.euroland.earningcalendar.model.date.DateDetails;
import com.euroland.earningcalendar.model.source.CrawlingSection;
import com.euroland.earningcalendar.model.source.ElementData;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.matcher.EventMatcherService;
import com.euroland.earningcalendar.util.thread.ThreadHandler;

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
	private static final String DEFAULT_IDENTIFIER = "default: ";

	private static final String CONCAT_IDENTIFIER = " concat: ";

	private static final String SELECTOR_IDENTIFIER = "([\\:\\/\\\">\\[\\]])";
	
	private static final String JOIN_IDENTIFIER = ":join:";

	private WebDriver webDriver = null;
	
	private String year;
	
	private String rowData;
	
	private List<List<HeaderValue>> headerValueList = new ArrayList<>();
	
	private static final Logger logger = LoggerFactory.getLogger(DataCrawlerService.class);
	
	public void dataLoader(WebDriver driver, PageConfig config) {
		
		webDriver = driver;
		
		year = null;
		
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
		
		ThreadHandler.sleep(5000);
		
		if(cs.getModifyElement() != null) {
			if (cs.getModifyElement().getType().equals("perLoad")) {
				modifyPerLoad(cs.getModifyElement());
			}
		}

		List<WebElement> wl = seleniumService.webElementsOut(
				webDriver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());

		wl.stream().forEach( w -> {

			rowData = "\n====================\n";
			
			List<HeaderValue> headerValue = new ArrayList<>();
			List<String> on = new ArrayList<>();
			
			try {
				if(cs.getModifyElement() != null) {
					if (cs.getModifyElement().getType().equals("perData")) {
						modifyPerData(w, cs.getModifyElement());
					}
				}
				
				if(cs.getDateDetails() != null) {
					// index 0 has the original date
					// index 1 has the modified date
					on = getDateDetail(w, cs, standardDate);
					
					String header = cs.getDateDetails().getFullDate().getName();
					
					// Add Header Value for Original Date
					headerValue.add(new HeaderValue(
							ORIGINAL + header, 
							on.get(0)));
					
					rowData = rowData + ORIGINAL + header + " --- " + on.get(0) + "\n";
					
					// Add Header Value of Modifed Date
					headerValue.add(new HeaderValue(
							header, 
							on.get(1)));
					
					rowData = rowData + header + " --- " + on.get(1) + "\n";

					List<HeaderValue> basicList = loadBasicDetails(cs.getBasicDetails(), w);
					headerValue.addAll(basicList);
					rowData = rowData + "====================";
					logger.debug(rowData);
				}
				
				
			} catch (Exception e) {
				if(on.size()>0) {
					if(w.isDisplayed()) {
						logger.error("Failed to Process Data Row: " + wl.indexOf(w)
								+ " Date: " + on.get(0)
								+ " Company: " + w.getText());
					} else {
						logger.error("Failed to Process Data Row: " + wl.indexOf(w)
								+ " Date: " + on.get(0));
					}
				} else {
					logger.error("Failed to Process Data Row: " + wl.indexOf(w));
				}
			}
			
			boolean status = checkData(headerValue);
			if(status) {
				headerValueList.add(headerValue);
			} else {
				logger.error("Failed to Add Data: " + headerValue.toString());
			}
		});
		
		logger.info("Unprocessed Data: " + headerValueList.size());
	}
	
	private void modifyPerLoad(ElementData ed) {
		WebElement we = seleniumService.webElementOut(webDriver, 
				ed.getSelector(), 
				ed.getSelectorType());
		if(we != null) {
			((JavascriptExecutor)webDriver).executeScript(
					"arguments[0].setAttribute(arguments[1], arguments[2]);", 
					we, ed.getRef(), ed.getName());
		}
	}
	
	private void modifyPerData(WebElement w, ElementData ed) {
		WebElement we = seleniumService.webElementOut(w, 
				ed.getSelector(), 
				ed.getSelectorType());

		if(we != null) {
			((JavascriptExecutor)webDriver).executeScript(
					"arguments[0].setAttribute(arguments[1], arguments[2]);", 
					we, ed.getRef(), ed.getName());
		}
	}
	
	private boolean checkData(List<HeaderValue> headerValue) {
		
		boolean status = false;
		
		if(headerValue.size() == 0)
			return status;
		
		HeaderValue date = headerValue.stream()
					.filter(e -> "Original Date".equals(e.getHeader())).findAny().orElse(null);
		
		if(date != null) {
			String dateValue = "";
			
			if (date != null) {
				dateValue = date.getValue();
			}
			
			if(!dateValue.equals("")) {
				status = true;
			}
			
		} else {
			status = false;
		}
		
		return status;
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
			
			// if there is a event
			if(header.toLowerCase().equals(EVENT.toLowerCase())) {
				// Add Header Value for Original Event Name
				header = ORIGINAL + EVENT;
				headerValue.add(new HeaderValue(header, value));
				rowData = rowData + header + " --- " + value + "\n";
				
				// Add Header Value for Modified Event Name
				header = EVENT;
				value = EventMatcherService.getEvent(value);
			}

			rowData = rowData + header + " --- " + value + "\n";
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
			
			if(year == null) {
				year = urltextOrAttribute(we, details.getSplitDate().stream()
				  .filter(e -> "year".equals(e.getName().toLowerCase())).findAny().orElse(null));
			}
			
			String month = urltextOrAttribute(we, details.getSplitDate().stream()
					  .filter(e -> "month".equals(e.getName().toLowerCase())).findAny().orElse(null));
			
			String day = urltextOrAttribute(we, details.getSplitDate().stream()
					  .filter(e -> "day".equals(e.getName().toLowerCase())).findAny().orElse(null));
			
			if(year.equals("")) {
				year = String.valueOf(LocalDate.now().getYear());
			}
			
			String previousDay = urltextOrAttribute(we, details.getSplitDate().stream()
					  .filter(e -> "previous day".equals(e.getName().toLowerCase())).findAny().orElse(null));
			
			String previousMonth = urltextOrAttribute(we, details.getSplitDate().stream()
					  .filter(e -> "previous month".equals(e.getName().toLowerCase())).findAny().orElse(null));

			String modMonth = DateMatcherService.modify(month);
			String modPreviousMonth = DateMatcherService.modify(previousMonth);
			
			date = year + "-" + modMonth + "-" + DateMatcherService.modify(day);
			
			if(date.contains(DateMatcherService.NO_MATCH_IDENTIFIER)) {
				date = "";
			} else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateMatcherService.DEFAULT_DATE_FORMAT_1, Locale.ENGLISH);
				LocalDate ld = LocalDate.parse(date, formatter);
				
				if(!previousDay.equals("") && (Integer.parseInt(day) < Integer.parseInt(previousDay))) {
					date = ld.plusMonths(1).format(formatter).toString();	
				} else if(!previousMonth.equals("") && (Integer.parseInt(modMonth) < Integer.parseInt(modPreviousMonth))) {
					date = ld.plusYears(1).format(formatter).toString();	
					year = Integer.toString(ld.plusYears(1).getYear());
				}
			}
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
		
		if(ed == null) {
			return result;
		}
		
		if(ed.getSelectorType() == null && (ed.getType() == null || !ed.getType().equals(URL_OUT))) {
			return ed.getSelector();
		}
		
		if(ed.getType().equals(URL_OUT)) {
			result = webDriver.getCurrentUrl().replaceAll("%20", " ");;
		}
		
		if(ed.getType().contains(ATTRIBUTE_OUT)) {
			result = seleniumService.attributeOut(we, ed.getSelector(), ed.getSelectorType(), ed.getRef());
		}
		
		if (result.equals("") && ed.getType().contains(TXT_OUT)){

			if(ed.getSelector().contains(CONCAT_IDENTIFIER)) {
				String[] selectors = ed.getSelector().split(CONCAT_IDENTIFIER);
				
				for(int ctr=0; ctr<selectors.length; ctr++) {
					
					result = result.trim();
					
					if(ed.getSplitter() != null && ed.getSplitter().contains(JOIN_IDENTIFIER)) {
						String[] join = ed.getSplitter().split(JOIN_IDENTIFIER);
						try {
							result = result + " " + isSplit(textOrSelector(we, selectors[ctr], ed.getSelectorType()).trim(), join[ctr], ed.getPosition());
						} catch (Exception e) {
							result = result + " " + textOrSelector(we, selectors[ctr], ed.getSelectorType()).trim();
						}
					} else {
						result = result + " " + textOrSelector(we, selectors[ctr], ed.getSelectorType()).trim();
					}
				}
			} else {
				result = seleniumService.textOut(we, ed.getSelector(), ed.getSelectorType());
			}
		}
		
		if(ed.getSplitter() == null || !ed.getSplitter().contains(JOIN_IDENTIFIER)) {
			result = isSplit(result.trim(), ed.getSplitter(), ed.getPosition());
		}
		
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
					String split = "";
					String def = "";
					if(splitter.contains(DEFAULT_IDENTIFIER)) {
						split = splitter.split(DEFAULT_IDENTIFIER)[1].split(REGEX_IDENTIFIER)[1];
						def = splitter.split(DEFAULT_IDENTIFIER)[1].split(REGEX_IDENTIFIER)[0];
					} else {
						split = splitter.split(REGEX_IDENTIFIER)[1];
					}
					
					Pattern p = Pattern.compile(split);
					Matcher m = p.matcher(text);

					if(m.find()) {
					    result = m.group(1);
					} else if(!def.equals("")){
						result = def;
					} else {
						result = "";
					}
				}
			} catch (Exception e) {
				result = "";
			}
		} 
		
		return result.trim();
	}
}
