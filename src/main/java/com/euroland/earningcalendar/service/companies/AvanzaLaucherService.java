package com.euroland.earningcalendar.service.companies;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.Conf;
import com.euroland.earningcalendar.model.CrawlSection;
import com.euroland.earningcalendar.service.LauncherService;

@Service("avanza")
public class AvanzaLaucherService extends LauncherService {

	@Override
	protected void sectionHandle(Conf conf, WebDriver driver) {

		System.out.println(driver.getTitle());

		// test case 10 clicks to load more data!
		for (int i = 0; i < 10; i++) {
			WebElement nextlick = driver.findElement(By.cssSelector("a.viewMoreLink"));
			nextlick.click();
		}
		
		this.crawl(conf, driver);

		driver.quit();
		
	}
	
	@Override
	protected void crawl(Conf conf, WebDriver driver) {
		for (CrawlSection cf : conf.getCrawlingSection()) {

			List<WebElement> allinks = seleniumService.webElementsOut(driver, cf.getSelector(), cf.getSelectorType());
			String event = "";
			for (WebElement w : allinks) {
				String companyName = "";
				String type = "";
				if (w.getTagName().equals("dd")) {
					List<WebElement> allcompanies = seleniumService.webElementsOut(w, "dd>.companyCalendarCountryItem>*", "cssSelector");
					for (WebElement wc : allcompanies) {
						try {
							type = wc.getText().split(" - ")[1].trim();
						} catch (Exception e) {

						}
						companyName = seleniumService.textOut(wc, "a", "cssSelector").trim();
						String url = seleniumService.attributeOut(wc, "a", "cssSelector", "href").trim();
						System.out.println(event + " -- " + companyName + " -- " + type);
						System.out.println("URL: " + url);

					}
				} else {
					event = w.getText();
				}

			}

		}
		
	}

}
