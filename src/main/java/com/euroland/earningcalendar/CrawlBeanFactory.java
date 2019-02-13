package com.euroland.earningcalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.SourceConfig;
import com.euroland.earningcalendar.service.companies.BenzingaLaucherService;
import com.euroland.earningcalendar.service.companies.CnbcLaucherService;
import com.euroland.earningcalendar.service.companies.LseLaucherService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Component
public class CrawlBeanFactory {
	
	@Autowired
	@Qualifier("default")
	private PagingCrawlerService pagingCrawlerService;
	
	@Autowired
	@Qualifier("lse")
	private LseLaucherService lseLaucherService;

	@Autowired
	@Qualifier("benzinga")
	private BenzingaLaucherService benzingaLaucherService;
	
	@Autowired
	@Qualifier("cnbc")
	private CnbcLaucherService cnbcLaucherService;
		
	public CrawlBeanFactory() {
		super();
	}

	public void getCrawl(SourceConfig sourceConfig) {
		
		switch (sourceConfig.getCname().toLowerCase()) {
		case "financial diary":
			lseLaucherService.appRunner(sourceConfig);
			break;
		case "benzinga":
			benzingaLaucherService.appRunner(sourceConfig);
			break;
		case "cnbc":
			cnbcLaucherService.appRunner(sourceConfig);
			break;
		default:
			pagingCrawlerService.appRunner(sourceConfig);
			break;
		}
	}

}
