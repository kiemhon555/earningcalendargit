package com.euroland.earningcalendar.selenium;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeleniumHandler {

	@Autowired
	ConnectionManager connectionManager;

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

	public boolean webElementClick(WebDriver driver, WebElement wbl) {
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

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				
			}
		}

		return ret;
	}

	/**
	 * Try open new page with little more delay and scroll down
	 *
	 * @param Webdriver
	 */
	public boolean pageChangeWithScrollDown(WebDriver driver, String link) throws InterruptedException {
		try {
			connectionManager.ConnectOnlyWhenOnline();
			System.out.println(link);
			driver.navigate().refresh();
			try {
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			} catch (Exception e1) {

			}

			try {
				driver.manage().deleteAllCookies();

				driver.get(link);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				driver.navigate().refresh();
			}
			Thread.sleep(2000);
			scrolldown(driver);

			Actions action = new Actions(driver);
			action.sendKeys(Keys.ESCAPE);

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
