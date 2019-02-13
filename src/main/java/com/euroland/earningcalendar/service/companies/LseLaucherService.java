package com.euroland.earningcalendar.service.companies;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.ElementBtn;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service("lse")
public class LseLaucherService extends PagingCrawlerService {
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
			
			List<ElementBtn> eb = config.getPagination();
			
			// Selecting month for filtering
			seleniumHandler.webElementClick(driver, 
					seleniumService.webElementOut(driver, 
						eb.get(0).getSelector(), 
						eb.get(0).getSelectorType()));
			
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
								eb.get(1).getSelectorType()));

					//  Clicking last day of the month
					seleniumHandler.webElementClick(driver, 
							seleniumService.webElementOut(driver, 
								eb.get(2).getSelector(), 
								eb.get(2).getSelectorType()));
					
					// Return to default page
					driver.switchTo().defaultContent();
				}

				loadData(driver, config);
			}
	}

}
