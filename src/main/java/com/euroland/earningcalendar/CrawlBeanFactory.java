package com.euroland.earningcalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.Config;
import com.euroland.earningcalendar.service.DefaultLauncherService;
import com.euroland.earningcalendar.service.companies.LseLaucherService;
import com.euroland.earningcalendar.service.companies.TmxLauncherService;

@Component
public class CrawlBeanFactory {
	
	@Autowired
	@Qualifier("default")
	private DefaultLauncherService defaulLaucherService;
	
	@Autowired
	@Qualifier("lse")
	private LseLaucherService lseLaucherService;
	
	@Autowired
	@Qualifier("tmx")
	private TmxLauncherService tmxLauncherService;
	
	public CrawlBeanFactory() {
		super();
	}

	public void getCrawl(Config config) {
		
		switch (config.getCname().toLowerCase()) {
		case "financial diary":
			lseLaucherService.appRunner(config);
			break;
		case "tmx money":
			tmxLauncherService.appRunner(config);
			break;
		default:
			defaulLaucherService.appRunner(config);
			break;
		}
	}

}
