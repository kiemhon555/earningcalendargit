package com.euroland.earningcalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.euroland.earningcalendar.model.Company;
import com.euroland.earningcalendar.service.CommonLaucherService;
import com.euroland.earningcalendar.service.companies.AvanzaLaucherService;
import com.euroland.earningcalendar.service.companies.CnbcLaucherService;

@Component
public class CrawlBeanFactory {
	
	@Autowired
	@Qualifier("common")
	private CommonLaucherService commonLaucherService;
	
	@Autowired
	@Qualifier("avanza")
	private AvanzaLaucherService avanzaLaucherService;
	
	@Autowired
	@Qualifier("cnbc")
	private CnbcLaucherService cnbcLaucherService;
	
	public CrawlBeanFactory() {
		super();
	}

	public void getCrawl(Company company) {
		switch (company.getCompanyName()) {
		case "avanza":
			avanzaLaucherService.appRunner(company.getConfigFile(), company.getCompanyUrl());
			break;
		case "cnbc":
			cnbcLaucherService.appRunner(company.getConfigFile(), company.getCompanyUrl());
			break;

		default:
			commonLaucherService.appRunner(company.getConfigFile(), company.getCompanyUrl());
			break;
		}
	}

}
