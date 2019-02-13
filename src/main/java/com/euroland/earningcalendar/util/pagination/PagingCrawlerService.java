package com.euroland.earningcalendar.util.pagination;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.ElementBtn;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.service.LauncherService;

@Service("default")
public class PagingCrawlerService extends LauncherService{

	// Index Marker is used for determining where will be the iteration in the selector
	private static final String INDEX_MARKER = "(euroland)";
	
	// To identify if need to click the button before recursion/load data
	private static final String FULL_CLICK_IDENTIFIER = "click all";

	// To identify if the button is a drop down type
	private static final String DROPDOWN_IDENTIFIER = "dropdown";
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
		if(config.getPagination() == null) {
			loadData(driver, config);
		
		} else {
			// 0 means its the first index in the pagination
			// size has -1 for the data gathering of the last index
			buttonInstance(driver, config, 0, config.getPagination().size()-1);
		}
	}

	// will convert the numeric string to int. if cant it will return 0
	private int isNumeric(String d)  
	{  
		int i = 0;
		try  {  
			i = Integer.parseInt(d);  
		} catch(NumberFormatException nfe) {  
		    return 0;  // not integer
		}  
		
		return i;  
	}
	
	public void buttonInstance(WebDriver driver, PageConfig config, int superCtr, int stop) {
		
			ElementBtn btn = config.getPagination().get(superCtr);
		
			// Will Check if the clicks of the button is dynamic or constant
			// value 0 if dynamic
			// value != if constant
			int no = isNumeric(btn.getClicks());
			
			
			// note that the for loop is looping constantly wether what happen to the button click
			// while the while loop will loop until the button fail to click
			
			if ( no != 0) { // constant click
				
				boolean clickOnLoad = btn.isClickOnLoad();
				
				// will get the index on config to now if the button incrementation is where to start
				// also will get the incrementation on config to modify the incrementation needed
				for (int ctr = btn.getIndex(); 
						ctr < no; ctr = ctr + btn.getIncrementation()) {

					String st = btn.getSelector(); // get the selector on config
					
					if(st.contains(INDEX_MARKER)) {
						// if it has a Index Marker it will modify the iteration base on the config
						st = st.replace(INDEX_MARKER, Integer.toString(ctr));
					}
					
					// This will check if the button is need to be clicked on load or not
					if (clickOnLoad) {
						
						// Get button element
						WebElement we = seleniumService.webElementOut(
								driver, st, btn.getSelectorType());
						
						if(!checkNullOrDisable(we) && !btn.getName().toLowerCase().contains("popup"))
							continue;

						String dd = btn.getName();
						if(dd.toLowerCase().contains(DROPDOWN_IDENTIFIER)) {
							
							seleniumHandler.webElementClick(driver, we.findElement(By.xpath(dd.split(" ")[1])));
						}
						
						boolean status = seleniumHandler.webElementClick(driver, we);
						if((!status && !btn.getName().toLowerCase().contains("popup"))) { // the loop will continue until its on the limit
							continue;
						}
						
					} else {
						// will set true so that it will now click button after load
						clickOnLoad = true;
					}
					
					// for continue clicking function (like Load More note: The "name" in config must be "Click All")
					if(btn.getName().toLowerCase().contains(FULL_CLICK_IDENTIFIER) && ctr < no - 1 )
						continue;
					
					if(superCtr < stop) { 
						// initiate buttonInstance
						buttonInstance(driver, config, superCtr + 1, stop);
						
						// change click load to its default if button instance is more than 1
						// 0 is the index so more than zero mean more than 1 button instance
						if(ctr >= no-1)
							clickOnLoad = btn.isClickOnLoad();
					} else {
						// Load Data
						loadData(driver, config);
					}
				}
			} else { // dynamic click

				int ctr = btn.getIndex(); // get index for ctr
				boolean status = true;
				boolean clickOnLoad = btn.isClickOnLoad();
				while(status) { 

					String st = btn.getSelector(); // get the selector on config
					st = st.replace(INDEX_MARKER, Integer.toString(ctr)); // replace index of the selector
					
					// This will check if the button is need to be clicked on load or not
					if (clickOnLoad) {
						
						// Get button element
						WebElement we = seleniumService.webElementOut(
								driver, st, btn.getSelectorType());
						
						if(!checkNullOrDisable(we))
							status = false;
						
						String dd = btn.getName();
						if(dd.toLowerCase().contains(DROPDOWN_IDENTIFIER)) {
							
							seleniumHandler.webElementClick(driver, we.findElement(By.xpath(dd.split(" ")[1])));
						}
						
						if(status) {
							
							status = seleniumHandler.webElementClick(driver, we);
							if((!status)) { // the loop will stop once the button cant click anymore
								status = false;
							}
							
							ctr = ctr + btn.getIncrementation(); // process the incrementation of the ctr
						}

					} else {
						// will set true so that it will now click button after load
						clickOnLoad = true;
					}
					
					// for continue clicking function (like Load More note: The "name" in config must be "Click All")
					if(status == true &&btn.getName().toLowerCase().contains(FULL_CLICK_IDENTIFIER)) {
						continue;

					} else if(status || (!status && btn.getName().toLowerCase().contains(FULL_CLICK_IDENTIFIER))) {
						if(superCtr < stop) {
							// instantiate buttonInstance until the maximum size of pagination/button types
							buttonInstance(driver, config, superCtr + 1, stop);
							
							// change click load to its default if button instance is more than 1
							// 0 is the index so more than zero mean more than 1 button instance
							if(!status)
								clickOnLoad = btn.isClickOnLoad();
						} else {
							// Load Data
							loadData(driver, config);
	
						}
					}
				}
			}
	}
	
	private boolean checkNullOrDisable(WebElement we) {
		boolean result = true;
		if(we == null) {
			// just stop the loop if element is null or disabled
			result = false;
		
		} else if(!we.isEnabled()|| we.getAttribute("class").toLowerCase().contains("disable")) {
			// just continue the loop if element is null or disabled
			result = false;
		}
		return result;
	}
}
