package com.euroland.earningcalendar.service.companies;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service("benzinga")
public class BenzingaLaucherService extends PagingCrawlerService {
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
		
		boolean status = false;
			
		List<ElementBtn> eb = config.getPagination();

		// Close Popup
		popupHandler.checkPopup(driver, eb.get(0));
	
		for (int ctr=0; ctr < Integer.parseInt(eb.get(1).getClicks()); ctr++) {
			
			// Open Calendar
			seleniumHandler.webElementClick(driver, 
					seleniumService.webElementOut(driver, 
						eb.get(1).getSelector(), 
						eb.get(1).getSelectorType()), 3000);
			if(ctr == 0) {

				WebElement fromDay = seleniumService.webElementOut(driver, 
						"//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')]/ancestor::tr/td[1]/a", 
						"xpath");
				
				seleniumHandler.webElementClick(driver, fromDay, 3000);
				
			} else {
				String selector = "//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')][last()]/ancestor::tr/following-sibling::tr/td[1]/a";
				if(ctr%2 != 0) {
					selector = "//*[@class='ui-datepicker-calendar']/tbody/tr[*]/td[*]/a[contains(@class,'ui-state-active')][last()]/ancestor::tr/td[4]/a";
				}
				
				WebElement toDay = seleniumService.webElementOut(driver, 
						selector, 
						"xpath");
				status = seleniumHandler.webElementClick(driver, toDay, 3000);
				
				if(!status) { // for next month click
					selector = "//*[@id='date-range-pick']/div/div[2]/table[@class='ui-datepicker-calendar']/tbody/tr[td[1][@data-event]][1]/td[1]/a";
					if(ctr%2 != 0) {
						selector = "//*[@id='date-range-pick']/div/div[2]/table[@class='ui-datepicker-calendar']/tbody/tr[td[4][@data-event]][1]/td[4]/a";
					}
					
					toDay = seleniumService.webElementOut(driver, 
							selector, 
							"xpath");
					seleniumHandler.webElementClick(driver, toDay, 3000);
				}
			}
		
			
			// Click Filter
			seleniumHandler.webElementClick(driver, 
					seleniumService.webElementOut(driver, 
							eb.get(2).getSelector(), 
							eb.get(2).getSelectorType()), 3000);
			
			// Load Data
			loadData(driver, config);
		}
	}

}
