package com.euroland.earningcalendar.service;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.Conf;

@Service("common")
public class CommonLaucherService extends LauncherService {

	@Override
	protected void sectionHandle(Conf conf, WebDriver driver) {
		crawl(conf, driver);

	}

}
