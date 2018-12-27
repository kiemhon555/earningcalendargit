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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SeleniumService {

	@Value(value = "${chrome.driver.location}")
	private String CHROME_DRIVER_LOCATION;

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
		// amenst.add("headless");
		amenst.add("window-size=3360x1890");
		amenst.add("--allow-insecure-localhost");
		amenst.add("--window-size=3360,1890");
		amenst.add("--dns-prefetch-disable");
		amenst.add("--javascript-harmony");
		amenst.add("--enable-link-disambiguation-popup");
		amenst.add("--no-sandbox");
		amenst.add("--disable-dev-shm-usage");

		ChromeOptions options = new ChromeOptions();
		options.addArguments(amenst);
		WebDriver driver = new ChromeDriver(options);
		TimeUnit.SECONDS.sleep(5);
		driver.manage().window().maximize();
		driver.manage().window().setSize(new Dimension(1920, 1080));
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Thread.sleep(1000); // Just wait in case

		return driver;
	}

	public String textOut(WebElement driver, String element, String elmType) {
		String ret = "";
		ret = webElementOut(driver, element, elmType).getText().toString();
		return ret;
	}

	public WebElement webElementOut(WebElement driver, String element, String elmType) {
		WebElement ret = null;
		try {
			if (elmType.equals("cssSelector")) {
				element.trim().replace(" ", ".");
				ret = driver.findElement(By.cssSelector(element));
			}
			if (elmType.equals("xpath"))
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
			// TODO Auto-generated catch block
			// e.printStackTrace();
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
			if (elmType.equals("xpath"))
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
		
		if(we != null)
				ret = we.getAttribute(attribute).toString();
		
		return ret;
	}

	public String textOut(WebDriver driver, String element, String elmType) {
		String ret = "";
		WebElement we = webElementOut(driver, element, elmType);
		if(we != null)
				ret = we.getText().toString();
			
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
			if (elmType.equals("xpath"))
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
			// TODO Auto-generated catch block
			// e.printStackTrace();
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
			if (elmType.equals("xpath"))
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
			System.out.println("Element type not found");
		}
		return ret;
	}

	public String attributeOut(WebDriver driver, String element, String elmType, String attribute) {
		String ret = "";
		ret = webElementOut(driver, element, elmType).getAttribute(attribute).toString();
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
