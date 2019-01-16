package com.euroland.earningcalendar.service.companies;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingSection;
import com.euroland.earningcalendar.model.ElementData;
import com.euroland.earningcalendar.model.HeaderValue;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.service.DefaultLauncherService;
import com.euroland.earningcalendar.util.data.DataCrawlerService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.matcher.EventMatcherService;

@Service("tmx")
public class TmxLauncherService extends DefaultLauncherService{

	@Autowired
	SeleniumService seleniumService;	
	
	@Autowired
	SeleniumHandler seleniumHandler;
	
	private List<List<HeaderValue>> headerValueList = new ArrayList<>();
	
	@Override
	protected boolean sectionHandle(WebDriver driver, PageConfig config) {
		
		boolean status = false;
		
		if(config != null) {
			
			driver.get(config.getWebsite());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int ctr = 0; ctr < Integer.parseInt(config.getPagination().get(0).getClicks()); ctr++) {
				// click first day
				WebElement fd = seleniumService.webElementOut(driver, 
						config.getPagination().get(0).getSelector(), 
						config.getPagination().get(0).getSelectorType());
				
				seleniumHandler.webElementClick(driver, fd);
				
				boolean loop = true;
				while (loop) {
					
//					System.out.println("Load Data : " + seleniumService.textOut(driver, "#leftside > div.datebar > ul > li.active", "cssSelector"));
//					loadData(driver, config);
					initializeData(driver, config);
					
					WebElement we = seleniumService.webElementOut(driver, 
							config.getPagination().get(1).getSelector(), 
							config.getPagination().get(1).getSelectorType());

					
					WebElement page = seleniumService.webElementOut(driver, 
							config.getPagination().get(2).getSelector(), 
							config.getPagination().get(2).getSelectorType());
					
					if (!page.getAttribute("class").contains("disable")) {
//						System.out.println("page click");
						// Page Click
						seleniumHandler.webElementClick(driver, page);
					} else {
//						System.out.println("day click");
						// Day Click		
						
						if(we.getText().equals("Next Week")) {
							loop = false;
						}
				
						((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight,0)");
						seleniumHandler.webElementClick(driver, we);
					}

				}
			}
			
			if (headerValueList.size() != 0) {
				status = true;
			}
		}
		
		return status;
	}
	
	protected void initializeData(WebDriver driver, PageConfig config) {
		
		CrawlingSection cs = config.getCrawlingSections().get(0);
		
		List<WebElement> wl = seleniumService.webElementsOut(driver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());
		
		for(WebElement w : wl) {
			
			List<HeaderValue> headerValue =  new ArrayList<>();
			
			String originalDate = driver.getCurrentUrl().split("date=")[1];
			headerValue.add(new HeaderValue(DataCrawlerService.ORIGINAL_DATE, originalDate));
			System.out.println(DataCrawlerService.ORIGINAL_DATE + " --- " + originalDate);
			
			String date = DateMatcherService.getDate(
					originalDate, "pattern-0", config.getStandardDate());
			headerValue.add(new HeaderValue("Date", date));
			System.out.println("Date --- " + date);
			
			for(ElementData b : cs.getBasicDetails()) {
	
				String header = b.getName();
				String value = "";
				
				// Get values from the Website
				if(b == cs.getBasicDetails().get(0)) { // Company name must be always on first array
					if(b.getType().equals(DataCrawlerService.ATTRIBUTE_OUT)) {
						value = w.getAttribute(b.getRef());
					} else {
						value = w.getText();
					}
					value = dataCrawlerService.isSplit(value, b.getSplitter(), b.getPosition());
				} else {
					value = dataCrawlerService.textOrAttribute(w, b);
				}
				
				// if there is a event
				if(header.toLowerCase().equals(DataCrawlerService.EVENT)) {
					// Add Header Value for Original Event Name
					header = DataCrawlerService.ORIGINAL_EVENT;
					headerValue.add(new HeaderValue(header, value));
					System.out.println(header + " --- " + value);
					
					// Add Header Value for Modified Event Name
					header = b.getName();
					value = EventMatcherService.getEvent(value);
				}
				System.out.println(header + " --- " + value);
				// Add Header Value for Basic Details
				headerValue.add(new HeaderValue(header, value));
			}

			System.out.println("===============================");
			headerValueList.add(headerValue);
		}
	}
}
