package com.euroland.earningcalendar.util.thread;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.logger.LoggerHandler;

@Component
public class ThreadHandler {

	@Autowired
	SeleniumHandler seleniumHandler;
	
	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	LoggerHandler logger;
	
	@Async
	public void checkPopup(WebDriver driver, ElementBtn btn) {
		try {
			while(driver != null) {
				
				WebElement we = seleniumService.webElementOut(driver, 
						btn.getSelector(), btn.getSelectorType());
				
				if(we != null) {
					boolean status = seleniumHandler.webElementClick(driver, we, 100);
					if(!status) {
						driver.switchTo().activeElement();
						status = seleniumHandler.webElementClick(driver, we, 100);
						driver.switchTo().defaultContent();
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Close Popup Thread: " + Thread.currentThread().getName());
		}
		
	}
	
	public static void sleep(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
