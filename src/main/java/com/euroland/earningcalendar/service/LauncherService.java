package com.euroland.earningcalendar.service;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.Conf;
import com.euroland.earningcalendar.model.CrawlSection;
import com.euroland.earningcalendar.model.ElementConf;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public abstract class LauncherService {

	@Autowired
	protected ConfService confService;

	@Autowired
	protected SeleniumService seleniumService;

	@Autowired
	protected SeleniumHandler seleniumHandler;

	public void appRunner(String config, String url) {
		
		Conf conf = null;
		try {
			conf = confService.prepareTestConf(config);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		WebDriver driver = null;
		try {
			driver = seleniumService.getDriver("");
			seleniumHandler.pageChangeWithScrollDown(driver, url);

			sectionHandle(conf, driver);
			driver.close();
			driver.quit();
		} catch (Exception e) {
			driver.close();
			driver.quit();
		}
		
	}
	
	protected abstract void sectionHandle(Conf conf, WebDriver driver);
	
	protected void crawl(Conf conf, WebDriver driver) {
		// loop sections from json configurtaion file
		for (CrawlSection cf : conf.getCrawlingSection()) {

			List<WebElement> crwls = seleniumService.weblementsOut(driver, cf.getSelector(), cf.getSelectorType());

			// loop Announcements from table
			for (WebElement w : crwls) {
				// loop crawlingelements
				for (ElementConf crElelemts : cf.getCrawlingElements()) {
					String tblFieldName = crElelemts.getName();
					String tblValue = "";
					if (crElelemts.getType().equals("txtOut"))
						tblValue = seleniumService.textOut(w, crElelemts.getSelector(), crElelemts.getSelectorType());
					System.out.println(tblFieldName + " " + tblValue);

				}

			}

		}
		
	}

}
