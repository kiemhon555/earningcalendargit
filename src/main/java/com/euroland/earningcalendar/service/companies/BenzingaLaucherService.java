package com.euroland.earningcalendar.service.companies;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingSection;
import com.euroland.earningcalendar.model.ElementBtn;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.model.data.HeaderValue;
import com.euroland.earningcalendar.util.data.DataCrawlerService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service("benzinga")
public class BenzingaLaucherService extends PagingCrawlerService {
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
		
		boolean status = false;
			
		List<ElementBtn> eb = config.getPagination();

		// Close Popup
		seleniumHandler.webElementClick(driver, 
				seleniumService.webElementOut(driver, 
					eb.get(0).getSelector(), 
					eb.get(0).getSelectorType()));
	
		
		for (int ctr=0; ctr < Integer.parseInt(eb.get(1).getClicks()); ctr++) {
			
			// Open Calendar
			seleniumHandler.webElementClick(driver, 
					seleniumService.webElementOut(driver, 
						eb.get(1).getSelector(), 
						eb.get(1).getSelectorType()));
			if(ctr == 0) {

				WebElement fromDay = seleniumService.webElementOut(driver, 
						"//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')]/ancestor::tr/td[1]/a", 
						"xpath");
				
				seleniumHandler.webElementClick(driver, fromDay);
				
			} else {
				String selector = "//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')][last()]/ancestor::tr/following-sibling::tr/td[1]/a";
				if(ctr%2 != 0) {
					selector = "//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')][last()]/ancestor::tr/td[4]/a";
				}
				
				WebElement toDay = seleniumService.webElementOut(driver, 
						selector, 
						"xpath");
				status = seleniumHandler.webElementClick(driver, toDay);
				
				if(!status) { // for next month click
					selector = "//*[@id='date-range-pick']/div/div[2]/table[@class='ui-datepicker-calendar']/tbody/tr[td[1][@data-event]][1]/td[1]/a";
					if(ctr%2 != 0) {
						selector = "//*[@id='date-range-pick']/div/div[2]/table[@class='ui-datepicker-calendar']/tbody/tr[td[4][@data-event]][1]/td[4]/a";
					}
					
					toDay = seleniumService.webElementOut(driver, 
							selector, 
							"xpath");
					seleniumHandler.webElementClick(driver, toDay);
				}
			}
		
			
			// Click Filter
			seleniumHandler.webElementClick(driver, 
					seleniumService.webElementOut(driver, 
							eb.get(2).getSelector(), 
							eb.get(2).getSelectorType()));
			
			// Load Data
			loadData(driver, config);
		}
	}
	
	@Override
	protected void loadData(WebDriver driver, PageConfig config) {
		
		List<List<HeaderValue>> headerValueList = new ArrayList<>();
		
		CrawlingSection cs = config.getCrawlingSections().get(0);
		
		List<WebElement> wl = seleniumService.webElementsOut(driver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());
		
		String yearString = seleniumService.textOut(driver, "#custom-dates > span.date-label.date-range-text", "cssSelector");
		
		Pattern pattern = Pattern.compile("(?:.*)(\\d{4})(?:.*)(\\d{4})", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(yearString);
		
		List<String> yearList = new ArrayList<>();
		
		if(matcher.find()) {
			yearList.add(matcher.group(1));
			yearList.add(matcher.group(2));
		}
		
		int prevDay = 0;
		
		String year = yearList.get(0);
		
		for(WebElement w : wl) {
			
			List<HeaderValue> headerValue =  new ArrayList<>();
			
			if(cs.getDateDetails() != null) {
				
				String date = seleniumService.textOut(w,
						cs.getDateDetails().getFullDate().getSelector(),
						cs.getDateDetails().getFullDate().getSelectorType());
				
				int day = Integer.parseInt(date.split(" ")[2]);
				
				if(day < prevDay) {
					year = yearList.get(1);
				}
				
				prevDay = day;
				
				date = date + " " + year;
				
				String header = cs.getDateDetails().getFullDate().getName();
				
				// Add Header Value for Original Date
				headerValue.add(new HeaderValue(
						DataCrawlerService.ORIGINAL + header, 
						date));
				System.out.println(DataCrawlerService.ORIGINAL + header + " --- " + date);
				

				date = DateMatcherService.getDate(date, cs.getDateDetails().getDatePattern(), config.getStandardDate());
				
				// Add Header Value of Modifed Date
				headerValue.add(new HeaderValue(
						header, 
						date));
				System.out.println(header + " --- " + date);

			}
			
			headerValue.addAll(dataCrawlerService.loadBasicDetails(cs.getBasicDetails(), w));

			System.out.println("===============================");
			headerValueList.add(headerValue);
		}
		
		System.out.println("unprocessed data: " + headerValueList.size());
		
		if (headerValueList.size() != 0) {
			dataCrawlerService.setCrawledData(headerValueList);
		}
		
	}

}
