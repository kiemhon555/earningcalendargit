package com.euroland.earningcalendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.euroland.earningcalendar.model.Config;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.model.date.DateConfig;
import com.euroland.earningcalendar.model.event.EventConfig;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.euroland.earningcalendar.util.db.DbService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.matcher.EventMatcherService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@SpringBootApplication
public class EarningCalendarApplication implements CommandLineRunner {
	
	@Autowired
	private CrawlBeanFactory crawlBeanFactory;
	
	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	DateMatcherService dateMatcherService;
	
	@Autowired
	EventMatcherService eventMatcherService;
	
	@Autowired
	ConfService confService;
	
	@Autowired
	DbService dbService;

	public static final String HOST = "http://10.10.18.62:2000";
	private static final String CONFIG_LINK = "/get_all_config";
	private static final String DATE_CONFIG_LINK = "/get_crawler_config_by_id/1";
	private static final String EVENT_CONFIG_LINK = "/get_crawler_config_by_id/2";

	private static final String DEFAULT_CONFIG_FILE = ".\\src\\main\\resources\\companies\\page_conf.json";
	private static final String DEFAULT_CONFIG_DATE_FILE = ".\\src\\main\\resources\\date\\date_conf.json";
	private static final String DEFAULT_CONFIG_EVENT_FILE = ".\\src\\main\\resources\\event\\event_conf.json";
	
	public static void main(String[] args) {
		SpringApplication.run(EarningCalendarApplication.class, args);
	}

	@Override
	public void run(String... args) {
		boolean status = false;
		
		// Load Date and Event config from api
		status = loadDateAndEventConfig();
		
		if (status) {

			List<Config> wl = null;

			String json = "";
			try {
				json = confService.readUrl(HOST + CONFIG_LINK);
				wl = new Gson().fromJson(json, new TypeToken<List<Config>>(){}.getType());
			} catch (Exception e) {
				System.out.println("Failed to Load Sources");
				return;
			}
			
			if (wl != null) {
				
				wl.stream().forEach( w -> {
					// Loading of each source
					// This condition is for demo purposes
					if(w.getCname().toLowerCase().equals("avanza")|| w.getCname().toLowerCase().equals("dfm")||w.getCname().toLowerCase().equals("teletrader"))
						crawlBeanFactory.getCrawl(w);
					
				});
			}
			
			// for resources location of page config
//			PageConfig pc = (PageConfig) confService.prepareTestConf(DEFAULT_CONFIG_FILE, new PageConfig());
//			
//			crawlBeanFactory.getCrawl(new Config(1, 2, "defs", pc));
		}
	}
	
	// Load DateConfig and EventConfig details from DB
	private boolean loadDateAndEventConfig() {
		boolean status = false;
		
		status = dateMatcherService.loadDateConfig(HOST + DATE_CONFIG_LINK);
		if(status) {
			status = eventMatcherService.loadEventConfig(HOST + EVENT_CONFIG_LINK);
		}
		
		return status;
	}
}
