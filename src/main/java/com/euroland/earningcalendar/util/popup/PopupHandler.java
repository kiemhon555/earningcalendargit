package com.euroland.earningcalendar.util.popup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;

@Component
public class PopupHandler {

	@Autowired
	SeleniumHandler seleniumHandler;
	
	@Autowired
	SeleniumService seleniumService;
	
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
//				System.out.println("popup thread: " + Thread.currentThread().getName());
			}
		} catch (Exception e) {
			System.out.println("close popup thread: " + Thread.currentThread().getName());
		}
		
	}
	
}
