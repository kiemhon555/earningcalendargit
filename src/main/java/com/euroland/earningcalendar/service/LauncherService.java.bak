package com.euroland.earningcalendar.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.CrawlingResult;
import com.euroland.earningcalendar.domain.model.HeaderValue;
import com.euroland.earningcalendar.model.source.ElementBtn;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.model.source.SourceConfig;
import com.euroland.earningcalendar.rabbit.Producer;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.data.DataCrawlerService;
import com.euroland.earningcalendar.util.db.DbService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service
public class LauncherService {

	@Autowired
	protected SeleniumService seleniumService;
	
	@Autowired
	protected SeleniumHandler seleniumHandler;
	
	@Autowired
	protected DataCrawlerService dataCrawlerService;
	
	@Autowired
	private DbService dbService;
	
	@Autowired
	private Producer producer;

	public boolean appRunner(SourceConfig c) {
		
		boolean status = false;
		
		WebDriver driver;
		try {
			driver = seleniumService.getDriver("");
		} catch (InterruptedException e) {
			return status;
		}
		
		// initialize crawled data list
		dataCrawlerService.setCrawledData(null);
		
		status = pageLoader(driver, c.getConfigText());
		
		if(status) {
			status = processResult(driver, c.getSourceId());
		}
		
		driver.close();
		
		return status;
	}

	public boolean pageLoader(WebDriver driver, PageConfig config) {
		
		boolean status = false;
		
		if(config == null) {
			return status;
		}

		String page = config.getWebsite();
		
		if(config.getWebsite().contains(PagingCrawlerService.INDEX_MARKER)) {
			LocalDate ld = LocalDate.now();
			ElementBtn btn = config.getPagination().stream()
					  .filter(e -> PagingCrawlerService.NAV_URL_IDENTIFIER.equals(e.getName().toLowerCase()))
					  .findAny().orElse(null);
			if (btn != null) {
				String date = ld.format(DateTimeFormatter.ofPattern(btn.getSelector()));
				page = config.getWebsite().replace(PagingCrawlerService.INDEX_MARKER, date);
			}
		}
		
		// Will Read The website on Config and load it
		status = seleniumHandler.pageChange(driver, page);
		
		if (config.getIframe() != null) { // If dont have an Iframe value in config it will just load the website
			page = seleniumService.attributeOut(driver, config.getIframe(), "cssSelector", "src");
			status = seleniumHandler.pageChange(driver, page);
		}
		
		if(status) {
			pageNavigation(driver, config);
		}

		if(dataCrawlerService.getCrawledData().size() != 0) {
			status = true;
		}
		
		return status;
	}

	protected void pageNavigation(WebDriver driver, PageConfig config) {}
	
	protected void loadData(WebDriver driver, PageConfig config) {
		dataCrawlerService.dataLoader(driver, config);
	}
	
	private boolean processResult(WebDriver driver, int sourceId) {
		
		boolean status = false;
		
		if(dataCrawlerService.getCrawledData().size() == 0) {
			System.out.println("No New Data Gathered");
			return status;
		}
		
		System.out.println("Filtering Duplicates ...");
		
		List<List<HeaderValue>> data = dbService.checkDuplicate(sourceId, dataCrawlerService.getCrawledData());

		System.out.println("Sending to Rabbit ...");
		
		if(data.size() != 0) {
			status = prepareSendingResult(sourceId, data);
	
			if (status) {
				System.out.println("Sent to Rabbit: " + data.size() + " Data");
			}
		} else {
			System.out.println("No Data Sent");
		}
		
		return status;
	}
	
	private boolean prepareSendingResult(int sourceId,List<List<HeaderValue>> headerValue) {
		
		boolean status = producer.produce(new CrawlingResult(sourceId, headerValue));
		
		return status;
	}
}
