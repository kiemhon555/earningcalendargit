package com.euroland.earningcalendar.selenium;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.util.logger.LoggerHandler;
import com.euroland.earningcalendar.util.thread.ThreadHandler;

@Service
public class SeleniumService {

	@Value(value = "${chrome.driver.location}")
	private String CHROME_DRIVER_LOCATION;

	@Autowired
	LoggerHandler logger;
	
	public int verifyURLStatus(String URL) {
		int statuscode = 200;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URL);
		try {
			HttpResponse response = client.execute(request);
			statuscode = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return statuscode;
	}

	public WebDriver getDriver(String headless) throws InterruptedException {

		System.out.println(CHROME_DRIVER_LOCATION);
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_LOCATION);
		List<String> amenst = new ArrayList<>();
		amenst.add("headless");
		amenst.add("window-size=3360x1890");
		amenst.add("--allow-insecure-localhost");
		amenst.add("--window-size=3360,1890");
		amenst.add("--dns-prefetch-disable");
		amenst.add("--javascript-harmony");
		amenst.add("--enable-link-disambiguation-popup");
		amenst.add("--no-sandbox");
		amenst.add("--disable-dev-shm-usage");
		amenst.add("--disable-notifications");

		ChromeOptions options = new ChromeOptions();
		options.addArguments(amenst);
		WebDriver driver = new ChromeDriver(options);
		TimeUnit.SECONDS.sleep(5);
		driver.manage().window().maximize();
		driver.manage().window().setSize(new Dimension(1920, 1080));
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		ThreadHandler.sleep(1000); // Just wait in case

		return driver;
	}

	public String textOut(WebElement driver, String element, String elmType) {
		String ret = "";
		
		if(elmType.equals("ownText")) {
			WebElement we = webElementOut(driver, element, "xpath");
			ret = we.getText();
			List<WebElement> childs = webElementsOut(we, "./*", "xpath");
			for(WebElement e : childs) {
				 ret = ret.replaceFirst(e.getText(), "");
			}
		} else {
			WebElement we = webElementOut(driver, element, elmType);
			if(we != null)
				ret = we.getText();
		}
		
		if(ret.matches("^[*!-.—_]+$")) {
			ret = "";
		}
		
		return ret.trim();
	}

	public WebElement webElementOut(WebElement driver, String element, String elmType) {
		WebElement ret = null;
		
		try {
			
			if (elmType.equals("cssSelector")) {
				element.trim().replace(" ", ".");
				ret = driver.findElement(By.cssSelector(element));
			}
			if (elmType.equals("xpath") || elmType.equals("ownText"))
				ret = driver.findElement(By.xpath(element));
			if (elmType.equals("className"))
				ret = driver.findElement(By.className(element));
			if (elmType.equals("id"))
				ret = driver.findElement(By.id(element));
			if (elmType.equals("linkText"))
				ret = driver.findElement(By.linkText(element));
			if (elmType.equals("name"))
				ret = driver.findElement(By.name(element));
			if (elmType.equals("partialLinkText"))
				ret = driver.findElement(By.partialLinkText(element));
			if (elmType.equals("tagName"))
				ret = driver.findElement(By.tagName(element));
		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	public List<WebElement> webElementsOut(WebElement driver, String element, String elmType) {
		List<WebElement> ret = new ArrayList<WebElement>();
		element = element.trim();
		try {
			if (elmType.equals("cssSelector")) {
				element.replace(" ", ".");
				ret = driver.findElements(By.cssSelector(element));
			}
			if (elmType.equals("xpath") || elmType.equals("ownText"))
				ret = driver.findElements(By.xpath(element));
			if (elmType.equals("className"))
				ret = driver.findElements(By.className(element));
			if (elmType.equals("id"))
				ret = driver.findElements(By.id(element));
			if (elmType.equals("linkText"))
				ret = driver.findElements(By.linkText(element));
			if (elmType.equals("name"))
				ret = driver.findElements(By.name(element));
			if (elmType.equals("partialLinkText"))
				ret = driver.findElements(By.partialLinkText(element));
			if (elmType.equals("tagName"))
				ret = driver.findElements(By.tagName(element));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return ret;
	}

	public String attributeOut(WebElement driver, String element, String elmType, String attribute) {
		String ret = "";
		WebElement we = webElementOut(driver, element, elmType);
		
		if(we != null) {
			try {
				ret = we.getAttribute(attribute);
			} catch (Exception e) {
				ret = "";
			}
			
			if(ret == null) {
				ret = "";
			}
		}
		
		return ret;
	}

	public String textOut(WebDriver driver, String element, String elmType) {
		String ret = "";
		WebElement we = webElementOut(driver, element, elmType);
		if(we != null)
				ret = we.getText();
			
		return ret;
	}

	public WebElement webElementOut(WebDriver driver, String element, String elmType) {
		WebElement ret = null;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			if (elmType.equals("cssSelector")) {
				element.trim().replace(" ", ".");
				ret = driver.findElement(By.cssSelector(element));
			}
			if (elmType.equals("xpath") || elmType.equals("ownText"))
				ret = driver.findElement(By.xpath(element));
			if (elmType.equals("className"))
				ret = driver.findElement(By.className(element));
			if (elmType.equals("id"))
				ret = driver.findElement(By.id(element));
			if (elmType.equals("linkText"))
				ret = driver.findElement(By.linkText(element));
			if (elmType.equals("name"))
				ret = driver.findElement(By.name(element));
			if (elmType.equals("partialLinkText"))
				ret = driver.findElement(By.partialLinkText(element));
			if (elmType.equals("tagName"))
				ret = driver.findElement(By.tagName(element));
		} catch (Exception e) {
			// return null if error or no element found
		}
		return ret;
	}

	public List<WebElement> webElementsOut(WebDriver driver, String element, String elmType) {
		List<WebElement> ret = new ArrayList<WebElement>();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		element = element.trim();
		try {
			if (elmType.equals("cssSelector")) {
				ret = driver.findElements(By.cssSelector(element));
			}
			if (elmType.equals("xpath") || elmType.equals("ownText"))
				ret = driver.findElements(By.xpath(element));
			if (elmType.equals("className"))
				ret = driver.findElements(By.className(element));
			if (elmType.equals("id"))
				ret = driver.findElements(By.id(element));
			if (elmType.equals("linkText"))
				ret = driver.findElements(By.linkText(element));
			if (elmType.equals("name"))
				ret = driver.findElements(By.name(element));
			if (elmType.equals("partialLinkText"))
				ret = driver.findElements(By.partialLinkText(element));
			if (elmType.equals("tagName"))
				ret = driver.findElements(By.tagName(element));
		} catch (Exception e) {
			logger.debug("Element type not found");
		}
		return ret;
	}

	public String attributeOut(WebDriver driver, String element, String elmType, String attribute) {
		String ret = "";
		WebElement we = webElementOut(driver, element, elmType);
		
		if(we != null) {
			try {
				ret = we.getAttribute(attribute);
			} catch (Exception e) {
				ret = "";
			}
			
			if(ret == null) {
				ret = "";
			}
		}
		return ret;
	}

	/**
	 * Get CSS value for class name
	 *
	 * @param Webdriver
	 * @param class_name
	 *            from document
	 * @param css_element_name
	 *            css style value
	 * @return String of css value
	 */
	public String getCSSvalue(WebDriver driver, String class_name, String css_element_name) {
		String ret = "";
		WebElement element = webElementOut(driver, class_name.replace(" ", "."), "cssSelector");
		if (element != null) {
			ret = element.getCssValue(css_element_name);
		}
		return ret;
	}

}
