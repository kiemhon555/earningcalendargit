package com.euroland.earningcalendar.service.companies;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.util.logger.LoggerHandler;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service("lse")
public class LseLaucherService extends PagingCrawlerService {
	
	@Autowired
	LoggerHandler logger;
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
		
		logger.debug("Initialize Page Navigation");
		logger.debug("Number Page Navigation: " + config.getPagination().size());
		
		List<ElementBtn> eb = config.getPagination();
			
		// Selecting month for filtering
		seleniumHandler.webElementClick(driver, 
				seleniumService.webElementOut(driver, 
					eb.get(0).getSelector(), 
					eb.get(0).getSelectorType()), 3000);
			
		for (int ctr = 0; ctr < Integer.parseInt(eb.get(1).getClicks()); ctr ++) {
			if (ctr != 0) {
				
				// Processing Iframe
				WebElement iframe = seleniumService.webElementOut(
				driver, "body > div.outerContainer > div > div.contentBackground > div.contentContainer > div.shareContent > div > div > div:nth-child(1) > iframe", "cssSelector");
				driver.switchTo().frame(iframe);
				
				// Clicking next month
				seleniumHandler.webElementClick(driver, 
						seleniumService.webElementOut(driver, 
							eb.get(1).getSelector(), 
							eb.get(1).getSelectorType()), 3000);

				//  Clicking last day of the month
				seleniumHandler.webElementClick(driver, 
						seleniumService.webElementOut(driver, 
							eb.get(2).getSelector(), 
							eb.get(2).getSelectorType()), 3000);
				
				// Return to default page
				driver.switchTo().defaultContent();
			}

			loadData(driver, config);
		}
	}

}
