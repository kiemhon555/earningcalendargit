package com.euroland.earningcalendar;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;

import com.euroland.earningcalendar.model.date.DateConfig;
import com.euroland.earningcalendar.model.event.EventConfig;
import com.euroland.earningcalendar.model.source.PageConfig;
import com.euroland.earningcalendar.model.source.SourceConfig;
import com.euroland.earningcalendar.selenium.SeleniumService;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.euroland.earningcalendar.util.db.DbService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.matcher.EventMatcherService;

@SpringBootApplication
@EnableAsync
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
	
	private static final String DEFAULT_CONFIG_FILE = ".\\src\\main\\resources\\companies\\page_conf.json";
	
	public static void main(String[] args) {
		
		SpringApplication.run(EarningCalendarApplication.class, args);
		
		System.exit(0);
		
		return;
	}
	
	@Override
	public void run(String... args) {
		boolean status = false;
		
		// Load Date and Event config from api
		status = loadDateAndEventConfig();
		
		if (status) {

			List<SourceConfig> wl = null;

			try {
				// Load Sources that status are active
				wl = confService.restTemplate.exchange(
						confService.HOST + confService.CONFIG_LINK, HttpMethod.GET, null,
						new ParameterizedTypeReference<List<SourceConfig>>() {}).getBody();
			} catch (Exception e) {
				System.out.println("Failed to Load Sources");
				return;
			}
			
			if (wl != null) {
				
				wl.stream().forEach( w -> {
					// Loading of each source
					// This condition is for demo purposes
//					if(w.getSourceId() != 32)
						crawlBeanFactory.getCrawl(w);
					
				});
			}
			
//			// for resources location of page config
//			PageConfig pc = (PageConfig) confService.prepareTestConf(DEFAULT_CONFIG_FILE, new PageConfig());
//			
//			crawlBeanFactory.getCrawl(new SourceConfig(34, 1, "def", pc));
		}
	}
	
	// Load DateConfig and EventConfig details from DB
	private boolean loadDateAndEventConfig() {
		boolean status = false;
		
		status = dateMatcherService.loadDateConfig(confService.HOST + confService.CONFIG_DATE_LINK);
		if(status) {
			status = eventMatcherService.loadEventConfig(confService.HOST + confService.CONFIG_EVENT_LINK);
		}
		
		return status;
	}
}
