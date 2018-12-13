package com.euroland.earningcalendar.service;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service
public class NewRunService {

	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	PagingCrawlerService pagingCrawlerService;
	
	
	private static final String PAGE_CONFIG_FILE = ".\\src\\main\\resources\\companies\\page_conf.json";

	public void run(WebDriver driver) throws Exception {
			crawl(driver);
	}
	
	private void crawl(WebDriver driver) throws Exception {
		
		pagingCrawlerService.pageLoader(driver, PAGE_CONFIG_FILE);

	}
}
