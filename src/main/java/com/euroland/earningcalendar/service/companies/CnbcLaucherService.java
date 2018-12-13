package com.euroland.earningcalendar.service.companies;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.Conf;
import com.euroland.earningcalendar.service.LauncherService;
import com.euroland.earningcalendar.model.CrawlSection;
import com.euroland.earningcalendar.model.ElementConf;

@Service("cnbc")
public class CnbcLaucherService extends LauncherService {

	@Override
	protected void sectionHandle(Conf conf, WebDriver driver) {

		// https://www.cnbc.com/earnings-calendar/
		// date wil be custom

		// 1 loop for 5 days of week

		// 1 IFRAME
		String iframeUrl = seleniumService.attributeOut(driver, "#IFRAME_ID0EXE15840257", "cssSelector", "src");
		try {
			seleniumHandler.pageChangeWithScrollDown(driver, iframeUrl);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int totalWeekCnt = 52;

		// week loop
		for (int wk = 0; wk < totalWeekCnt; wk++) {

			try {
				WebElement weeks = driver.findElement(By.xpath("//div/a[contains(text(),'Next Week')]"));
				if (wk > 0) {
					weeks.click();
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<WebElement> days = seleniumService.webElementsOut(driver, "#dayContainer>div>div>div>a", "cssSelector");

			// count how many next clicks exist
			List<WebElement> nextEvents = seleniumService.webElementsOut(driver, "#pagination>a", "cssSelector");
			int nextEventUrlsClicks = nextEvents.size() + 1;

			// loop days per week
			for (WebElement d : days) {
				d.click();

				String dateSelector = seleniumService.textOut(driver, "#head0", "cssSelector") + " " + seleniumService.textOut(driver, "#month>#month0", "cssSelector"); // "12 NOVEMBER 2018"

				// event table click next ...
				for (int clCnt = 1; clCnt <= nextEventUrlsClicks; clCnt++) {

					if (clCnt > 1) {
						WebElement cl3 = driver.findElement(By.xpath("//div[@id='pagination']/a[text()='" + clCnt + "']"));
						cl3.click();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					crawl(conf, driver);
					// next click if no more, hasNext => false
				}
			}
		}
		
	}

}
