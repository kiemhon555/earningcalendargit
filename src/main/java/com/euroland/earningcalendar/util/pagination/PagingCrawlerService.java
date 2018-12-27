package com.euroland.earningcalendar.util.pagination;

import java.time.LocalDate;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.data.DataCrawlerService;

@Service
public class PagingCrawlerService {
	
	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	SeleniumHandler seleniumHandler;
	
	@Autowired
	DataCrawlerService dataCrawlerService;

	
	// Index Marker is used for determining where will be the iteration in the selector
	private static final String INDEX_MARKER = "(euroland)";
	
	// To identify if need to click the button before recursion/load data
	private static final String FULL_CLICK_IDENTIFIER = "Click All";

	// To identify start date if have for the url loading
	private static final String START_DATE_EURO = "(startDateEuro)";

	// To identify end date if have for the url loading
	private static final String END_DATE_EURO = "(endDateEuro)";
	
	public void pageLoader(WebDriver driver, PageConfig config) {
		
		String page = config.getWebsite();
		
		// Will Read The website on Config and load it
		driver.get(urlCheck(page));
		
		if (!config.getIframe().equals("")) { // If dont have an Iframe value in config it will just load the website
			page = seleniumService.attributeOut(driver, config.getIframe(), "cssSelector", "src");
			driver.get(page);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 10 sec wait time for popup
		
		pageChecker(driver, config);
	}
	
	public void pageChecker(WebDriver driver, PageConfig config) {
		if(config.getPagination().size() == 0) {
			dataCrawlerService.dataLoader(driver, config);
		
		} else if (config.getPagination().size() > 0){
			// Will return the data from Reygie
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
		
			// Will Check if the clicks of the button is dynamic or constant
			// value 0 if dynamic
			// value != if constant
			int no = isNumeric(config.getPagination().get(superCtr).getClicks());
			
			
			// note that the for loop is looping constantly wether what happen to the button click
			// while the while loop will loop until the button fail to click
			
			if ( no != 0) { // constant click
				
				boolean clickOnLoad = config.getPagination().get(superCtr).isClickOnLoad();
				
				// will get the index on config to now if the button incrementation is where to start
				// also will get the incrementation on config to modify the incrementation needed
				for (int ctr = config.getPagination().get(superCtr).getIndex(); 
						ctr < no; ctr = ctr + config.getPagination().get(superCtr).getIncrementation()) {

					String st = config.getPagination().get(superCtr).getSelector(); // get the selector on config
					
					if(st.contains(INDEX_MARKER)) {
						// if it has a Index Marker it will modify the iteration base on the config
						st = st.replace(INDEX_MARKER, Integer.toString(ctr));
					}
					
					// This will check if the button is need to be clicked on load or not
					if (clickOnLoad) {
						
						// Get button element
						WebElement we = seleniumService.webElementOut(
								driver, st, config.getPagination().get(superCtr).getSelectorType());
						
						if(!checkNullOrDisable(we))
							continue;
						
						boolean status = seleniumHandler.webElementClick(we);
						if(!status) { // the loop will stop once the button cant click anymore
							continue;
						}
						
					} else {
						// will set true so that it will now click button after load
						clickOnLoad = true;
					}
					
					// for continue clicking function (like Load More note: The "name" in config must be "Click All")
					if(config.getPagination().get(superCtr).getName().equals(FULL_CLICK_IDENTIFIER) && ctr < no - 1 )
						continue;
					
					if(superCtr < stop) { 
						
						// initiate buttonInstance
						buttonInstance(driver, config, superCtr + 1, stop);
						
						// change click load to its default if button instance is more than 1
						// 0 is the index so more than zero mean more than 1 button instance
						if(superCtr > 1)
							clickOnLoad = config.getPagination().get(superCtr).isClickOnLoad();
						
					} else {
						// Load Data
						
						loadData(driver, config);
					}
				}
			} else { // dynamic click
				
				int ctr = config.getPagination().get(superCtr).getIndex(); // get index for ctr
				boolean status = true;
				boolean clickOnLoad = config.getPagination().get(superCtr).isClickOnLoad();
				while(status) { 

					String st = config.getPagination().get(superCtr).getSelector(); // get the selector on config
					st = st.replace(INDEX_MARKER, Integer.toString(ctr)); // replace index of the selector
					
					// This will check if the button is need to be clicked on load or not
					if (clickOnLoad) {
						
						// Get button element
						WebElement we = seleniumService.webElementOut(
								driver, st, config.getPagination().get(superCtr).getSelectorType());
						
						if(!checkNullOrDisable(we))
							status = false;
						
						if(status) {
							status = seleniumHandler.webElementClick(we);
							if(!status) { // the loop will stop once the button cant click anymore
								status = false;
							}
							
							ctr = ctr + config.getPagination().get(superCtr).getIncrementation(); // process the incrementation of the ctr
						}

					} else {
						// will set true so that it will now click button after load
						clickOnLoad = true;
					}
					
					// for continue clicking function (like Load More note: The "name" in config must be "Click All")
					if(status == true && config.getPagination().get(superCtr).getName().equals(FULL_CLICK_IDENTIFIER)) {
						continue;

					} else if(status || (!status && config.getPagination().get(superCtr).getName().equals(FULL_CLICK_IDENTIFIER))) {
						if(superCtr < stop) {
							// instantiate buttonInstance until the maximum size of pagination/button types
							buttonInstance(driver, config, superCtr + 1, stop);
							
							// change click load to its default if button instance is more than 1
							// 0 is the index so more than zero mean more than 1 button instance
							if(superCtr > 1)
								clickOnLoad = config.getPagination().get(superCtr).isClickOnLoad();
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
	
	private String urlCheck(String page) {
		
		if(page.contains("(endDateEuro)")) {
			
			LocalDate ld = LocalDate.now();
			
			page = page.replace(START_DATE_EURO, ld.toString());
			page = page.replace(END_DATE_EURO, ld.plusMonths(3).toString());
			
		}
		
		return page;
	}
	
	private void loadData(WebDriver driver, PageConfig config) {	
		
		dataCrawlerService.dataLoader(driver, config);
	}
}
