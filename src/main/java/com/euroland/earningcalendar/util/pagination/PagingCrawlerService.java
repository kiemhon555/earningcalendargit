package com.euroland.earningcalendar.util.pagination;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.service.LauncherService;
import com.euroland.earningcalendar.util.thread.ThreadHandler;

@Service("default")
public class PagingCrawlerService extends LauncherService{

	// Index Marker is used for determining where will be the iteration in the selector
	public static final String INDEX_MARKER = "(euroland)";
	
	// To identify if need to click the button before recursion/load data
	private static final String FULL_CLICK_IDENTIFIER = "click all";

	// To identify if the button is a drop down type
	private static final String DROPDOWN_IDENTIFIER = "dropdown";
	
	// To identify if the button is on a dialog
	private static final String DIALOG_IDENTIFIER = "dialog";

	// To identify if the button is a PopUp
	private static final String POPUP_IDENTIFIER = "popup";
	
	// To identify if the button is for navigating the url
    public static final String NAV_URL_IDENTIFIER = "navigate url";
	
    private static final Logger logger = LoggerFactory.getLogger(PagingCrawlerService.class);
    
	@Autowired
	protected ThreadHandler threadHandler;
	
	@Override
	protected void pageNavigation(WebDriver driver, PageConfig config) {
		
		logger.debug("Checking Page Navigation ...");
		
		if(config.getPagination() == null) {
			logger.debug("No Page Navigation Identified");
			loadData(driver, config);
		} else {
			logger.debug("Number Page Navigation: " + config.getPagination().size());
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
		String button = btn.getName();
		logger.debug("Initialize Navigation: " + button);
		
		// Will Check if the clicks of the button is dynamic or constant
		// value 0 if dynamic
		// value != if constant
		int no = isNumeric(btn.getClicks());
			
		int incrementation = 1;
		if(btn.getIncrementation() != 0)
			incrementation = btn.getIncrementation();
			
		// note that the for loop is looping constantly wether what happen to the button click
		// while the while loop will loop until the button fail to click
			
			if ( no != 0) { // constant click
				
				boolean clickOnLoad = btn.isClickOnLoad();
				
				// will get the index on config to now if the button incrementation is where to start
				// also will get the incrementation on config to modify the incrementation needed
				for (int ctr = btn.getIndex(); 
						ctr < no; ctr = ctr + incrementation) {

					
					
					String st = btn.getSelector(); // get the selector on config
					

					// This will check if the button is need to be clicked on load or not
					if (clickOnLoad) {
						
						if(btn.getName().toLowerCase().equals(NAV_URL_IDENTIFIER)) {
							String d = "";
							Pattern p = Pattern.compile(btn.getSelectorType());
							Matcher m = p.matcher(driver.getCurrentUrl());
	
						if(m.find()) {
						    d = m.group(1);
							LocalDate ld = LocalDate.parse(d, DateTimeFormatter.ofPattern(st)).plusDays(1);
							String page = config.getWebsite().replace(INDEX_MARKER, 
									ld.format(DateTimeFormatter.ofPattern(st))).toString();
							seleniumHandler.pageChange(driver, page);
						}
						
					} else {
						// if it has a Index Marker it will modify the iteration base on the config
						if(st.contains(INDEX_MARKER)) {
							st = st.replace(INDEX_MARKER, Integer.toString(ctr));
						}
						
						// Get button element
						WebElement we = seleniumService.webElementOut(
								driver, st, btn.getSelectorType());
						
						if(btn.getName().toLowerCase().contains(POPUP_IDENTIFIER)) {
							threadHandler.checkPopup(driver, btn);
						} else {

							if(!checkNullOrDisable(we) && !btn.getName().toLowerCase().contains(DIALOG_IDENTIFIER))
								continue;
		
							if(button.toLowerCase().contains(DROPDOWN_IDENTIFIER)) {
								seleniumHandler.webElementClick(driver, we.findElement(By.xpath(button.split(" ")[1])), 3000);
							}
							
							boolean status = seleniumHandler.webElementClick(driver, we, 3000);
							if((!status) && !button.toLowerCase().contains(DIALOG_IDENTIFIER)) { // the loop will continue until its on the limit
								continue;
							}
						}
					}
					
					
				} else {
					// will set true so that it will now click button after load
					clickOnLoad = true;
				}
				
				// for continue clicking function (like Load More note: The "name" in config must be "Click All")
				if(button.toLowerCase().contains(FULL_CLICK_IDENTIFIER) && ctr < no - 1 )
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
					
					if(button.toLowerCase().contains(DROPDOWN_IDENTIFIER)) {
						seleniumHandler.webElementClick(driver, we.findElement(By.xpath(button.split(" ")[1])), 3000);
					}
					
					if(status) {
						
						status = seleniumHandler.webElementClick(driver, we, 3000);
						if((!status)) { // the loop will stop once the button cant click anymore
							status = false;
						}
						
						ctr = ctr + incrementation; // process the incrementation of the ctr
					}

				} else {
					// will set true so that it will now click button after load
					clickOnLoad = true;
				}
				
				// for continue clicking function (like Load More note: The "name" in config must be "Click All")
				if(status == true && button.toLowerCase().contains(FULL_CLICK_IDENTIFIER)) {
					continue;

				} else if(status || (!status && button.toLowerCase().contains(FULL_CLICK_IDENTIFIER))) {
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
