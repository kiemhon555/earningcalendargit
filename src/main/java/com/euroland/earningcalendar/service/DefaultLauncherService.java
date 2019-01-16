package com.euroland.earningcalendar.service;


import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.Config;
import com.euroland.earningcalendar.model.CrawlingResult;
import com.euroland.earningcalendar.model.HeaderValue;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.selenium.SeleniumHandler;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.data.DataCrawlerService;
import com.euroland.earningcalendar.util.db.DbService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;
import com.euroland.earningcalendar.util.rabbit.Producer;

@Service("default")
public class DefaultLauncherService {

	@Autowired
	protected SeleniumService seleniumService;
	
	@Autowired
	protected SeleniumHandler seleniumHandler;
	
	@Autowired
	protected PagingCrawlerService pagingCrawlerService;
	
	@Autowired
	protected DataCrawlerService dataCrawlerService;
	
	@Autowired
	private DbService dbService;
	
	@Autowired
	private Producer producer;

	public boolean appRunner(Config c) {
		
		boolean status = false;
		
		WebDriver driver;
		try {
			driver = seleniumService.getDriver("");
		} catch (InterruptedException e) {
			return status;
		}
		
		// initialize crawled data list
		dataCrawlerService.setCrawledData(new ArrayList<>());
		
		status = sectionHandle(driver, c.getConfig_text());
		
		if(status) {
			status = processResult(driver, c.getSource_id());
		}
		
		driver.close();
		
		return status;
	}

	protected boolean sectionHandle(WebDriver driver, PageConfig config) {
		
		boolean status = false;
		
		if(config != null) {
			pagingCrawlerService.pageLoader(driver, config);
			status = true;
		}
		
		return status;
	}

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
		
		status = prepareResult(sourceId, data);

		if (status) {
			System.out.println("Sent to Rabbit");
		}
		
		return status;
	}
	
	private boolean prepareResult(int sourceId,List<List<HeaderValue>> headerValue) {
		
		boolean status = producer.produce(new CrawlingResult(sourceId, headerValue));
		
		return status;
	}
}
