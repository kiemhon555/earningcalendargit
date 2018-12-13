package com.euroland.earningcalendar.util.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingSection;
import com.euroland.earningcalendar.model.DateDetails;
import com.euroland.earningcalendar.model.ElementData;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.date.DateMatcher;

@Service
public class DataCrawlerService {

	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	DateMatcher dateMatcher;
	
	private static final String ATTRIBUTE_OUT = "attributeOut";
	
	public void dataLoader(WebDriver driver, PageConfig config) {
		
		config.getCrawlingSections().forEach( c -> {
			
			loadData(driver, c, config.getStandardDate());
			
		});
		
//		loadData(driver, config);
		
//		dateMatcher.getDate(" asfasf Jan 2, 2018 adsfasf", "pattern-1", "MM/dd/yyyy");// 1 c
//		dateMatcher.getDate("ads12 january 2018", "pattern-2", "MM/dd/yyyy"); //2 c
//		dateMatcher.getDate("2018 july 2", "pattern-3", "MM/dd/yyyy"); // 3 c
//		dateMatcher.getDate("2018 enero 2", "pattern-3", "dd/MM/yyyy");
//		dateMatcher.getDate("12-31-2019", "pattern-4", "MM/dd/yyyy");
//		dateMatcher.getDate("jan/02 18", "pattern-1", "MM/dd/yyyy");// 1 c
//		dateMatcher.getDate("2018/12/12", "pattern-0", "MM/dd/yyyy"); // 4
//		dateMatcher.getDate("29-12-2018", "pattern-5", "MM/dd/yyyy");
//		dateMatcher.getDate("31.10.19 asdasd", "pattern-5", "MM/dd/yyyy");
//		dateMatcher.getDate("as29,2,19 asdasd", "pattern-5", "MM/dd/yyyy");
		
	}
	
	
	private void loadData(WebDriver driver, CrawlingSection cs, String standardDate) {
		
		List<WebElement> wl = seleniumService.webElementsOut(
				driver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());
		
		for(WebElement we : wl) {
			
			System.out.println(
				cs.getDateDetails().getFullDate().getName() + " - " + getDateDetail(we, cs, standardDate));
			
			for(int ctr=0; ctr < cs.getBasicDetails().size(); ctr++) {
	
				String field = cs.getBasicDetails().get(ctr).getName();
				String value = "";
				
				if(ctr == 0) {
					if(cs.getBasicDetails().get(ctr).getType().equals(ATTRIBUTE_OUT)) {
						value = we.getAttribute(cs.getBasicDetails().get(ctr).getRef());
					} else {
						value = we.getText();
					}
					value = isSplit(value, cs.getBasicDetails().get(ctr).getSplitter(), cs.getBasicDetails().get(ctr).getPosition());
				} else {
					value = textOrAttribute(we, cs.getBasicDetails().get(ctr));
				}
				
				System.out.println(field + " - " + value);
			}
			
			System.out.println("===============================");
		}
		
	}
	
	private String getDateDetail(WebElement we, CrawlingSection cs, String standardDate) {

		String date = "";

		String pattern = "";

		DateDetails details = cs.getDateDetails();
		
		
		if(details.getSplitDate().size() ==0 ) {
			date = textOrAttribute(we, details.getFullDate());
			pattern = details.getDatePattern();
			
		} else {
			String year = textOrAttribute(we, details.getSplitDate().get(0));
			String month = textOrAttribute(we, details.getSplitDate().get(1));
			String day = textOrAttribute(we, details.getSplitDate().get(2));
			
			date = year + "-" + month + "-" + day;
		}
		
		date = dateMatcher.getDate(
				date, pattern, standardDate);
		
		return date;
	}
	
	private String textOrAttribute(WebElement we, ElementData ed) {
		
		String result = "";
		
		if(ed.getType().equals(ATTRIBUTE_OUT)) {
			result = seleniumService.attributeOut(we, ed.getSelector(), ed.getSelectorType(), ed.getRef());
		} else {
			result = seleniumService.textOut(we, ed.getSelector(), ed.getSelectorType());
		}
		
			result = isSplit(result, ed.getSplitter(), ed.getPosition());
		
		return result;
	}
	
	private String isSplit(String text, String splitter, int pos) {
		String result = text;
		
		if(!splitter.equals("")) {
			result = text.split(splitter)[pos];
		} 
		
		return result;
	}
}
