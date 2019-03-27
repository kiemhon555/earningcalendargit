package com.euroland.earningcalendar.selenium;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.util.thread.ThreadHandler;

@Service
public class SeleniumHandler {

	@Autowired
	ConnectionManager connectionManager;

	private static final Logger logger = LoggerFactory.getLogger(SeleniumHandler.class);
	
	public void scrolldown(WebDriver d) throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) d;
		try {
			jse.executeScript("window.scrollBy(0,100)", "");
			Thread.sleep(1000);
			jse.executeScript("window.scrollBy(0,100)", "");
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		try {
			jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			Thread.sleep(1000);
		} catch (Exception e) {
		}
	}

	public boolean webElementClick(WebDriver driver, WebElement wbl, int delay) {
		boolean ret = true;

		try {
			wbl.click();
		} catch (Exception e) {
			try {
				wbl.sendKeys(Keys.LEFT_CONTROL);
				wbl.click();
			} catch (Exception e1) {
				try {
					((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight,0)");
					wbl.click();
				} catch (Exception e2) {
					return false;
				}
			}
		} finally {
			ThreadHandler.sleep(delay);
		}

		return ret;
	}

	/**
	 * Try open new page with little more delay and scroll down
	 *
	 * @param Webdriver
	 */
	public boolean pageChange(WebDriver driver, String link) {
		try {
			connectionManager.ConnectOnlyWhenOnline();
			driver.navigate().refresh();
			try {
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			} catch (Exception e1) {

			}

			boolean status = true;
			int retry = 1;
			while (status && retry != 4) {
				try {
					driver.manage().deleteAllCookies();
	
					logger.info("Loading Page: " + link);
					driver.get(link);
					// if true it means the page is empty and need to be reloaded. max retries is 3 times
					status = driver.getPageSource().contains("<head></head><body></body></html>");
					
					if(status) {
						logger.error("Page Not Loaded");
						logger.error("Retries: " + retry);
						Thread.sleep(5000);
						retry++;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					driver.navigate().refresh();
				}
			}
			
			if(status) {
				logger.error("Page Can't be Loaded");
				return false;
			}
			
			ThreadHandler.sleep(10000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Try closing popup window
	 *
	 * @param Webdriver
	 */
	public void tryToclosePopup(WebDriver driver) {
		String parent = driver.getWindowHandle();

		try {
			Alert alert = driver.switchTo().alert();
			// Prints text and closes alert
			System.out.println(alert.getText());
			alert.accept();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<String> pops = driver.getWindowHandles();
		{
			Iterator<String> it = pops.iterator();
			while (it.hasNext()) {
				String popupHandle = it.next().toString();
				if (!popupHandle.contains(parent)) {
					driver.switchTo().window(popupHandle);
					System.out.println("Popu Up Title: " + driver.switchTo().window(popupHandle).getTitle());
					driver.close();
				}

			}
		}

	}

}
