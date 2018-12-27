package com.euroland.earningcalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.Config;
import com.euroland.earningcalendar.service.DefaultLauncherService;
import com.euroland.earningcalendar.service.companies.CnbcLaucherService;

@Component
public class CrawlBeanFactory {
	
	@Autowired
	@Qualifier("default")
	private DefaultLauncherService defaulLaucherService;
	
	@Autowired
	@Qualifier("cnbc")
	private CnbcLaucherService cnbcLaucherService;
	
	public CrawlBeanFactory() {
		super();
	}

	public void getCrawl(Config config) {
		
		switch (config.getCname()) {
		case "test1":
			cnbcLaucherService.appRunner(config);
			break;

		default:
			defaulLaucherService.appRunner(config);
			break;
		}
	}

}
