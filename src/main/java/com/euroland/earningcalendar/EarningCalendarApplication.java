package com.euroland.earningcalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.euroland.earningcalendar.model.Company;

@SpringBootApplication
public class EarningCalendarApplication implements CommandLineRunner {
	
	@Autowired
	private CrawlBeanFactory crawlBeanFactory;

	public static void main(String[] args) {
		SpringApplication.run(EarningCalendarApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Company company = null;
		
		String companyName = "avanza";
		String configFile = ".\\src\\main\\resources\\companies\\avanza_conf.json";
		String companyUrl = "https://www.avanza.se/placera/foretagskalendern.html";
		company = new Company(companyName, configFile, companyUrl);

		companyName = "cnbc";
		configFile = ".\\src\\main\\resources\\companies\\cnbc_conf.json";
		companyUrl = "https://www.cnbc.com/earnings-calendar/";
		company = new Company(companyName, configFile, companyUrl);
		crawlBeanFactory.getCrawl(company);
	}
}
