package com.euroland.earningcalendar.service.companies;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingSection;
import com.euroland.earningcalendar.model.HeaderValue;
import com.euroland.earningcalendar.model.PageConfig;
import com.euroland.earningcalendar.util.data.DataCrawlerService;
import com.euroland.earningcalendar.util.matcher.DateMatcherService;
import com.euroland.earningcalendar.util.pagination.PagingCrawlerService;

@Service("cnbc")
public class CnbcLaucherService extends PagingCrawlerService {
	
	@Override
	protected void loadData(WebDriver driver, PageConfig config) {
		
		List<List<HeaderValue>> headerValueList = new ArrayList<>();
		
		CrawlingSection cs = config.getCrawlingSections().get(0);
		
		List<WebElement> wl = seleniumService.webElementsOut(driver, cs.getBasicDetails().get(0).getSelector(), cs.getBasicDetails().get(0).getSelectorType());
		
		if(wl.size() == 0) {
			return;
		}
		String origDate="";
		
		if(cs.getDateDetails() != null) {

			origDate = dataCrawlerService.urltextOrAttribute(wl.get(0), cs.getDateDetails().getFullDate()).toLowerCase();
			
			String[] tempDate = origDate.split(" ");
			tempDate[1] = tempDate[1].substring(0, 1).toUpperCase() + tempDate[1].substring(1);
			
			origDate = tempDate[0] + "-" + tempDate[1] + "-" + tempDate[2];
			
			WebElement currentDay = seleniumService.webElementOut(wl.get(0), "//*[@id=\"dayContainer\"]/div[contains(@class,'day on')]/h6/b", "xpath");
			WebElement prevDay = seleniumService.webElementOut(wl.get(0), "//*[@id=\"dayContainer\"]/div[contains(@class,'day on')]/preceding-sibling::div/h6/b", "xpath");
			if(prevDay != null && currentDay != null && (Integer.parseInt(currentDay.getText())) < Integer.parseInt(prevDay.getText())) {
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy", Locale.ENGLISH);
				LocalDate ld = LocalDate.parse(origDate, formatter);
				origDate = ld.plusMonths(1).format(formatter).toString();
			}
		}
		
		for(WebElement w : wl) {
			
			List<HeaderValue> headerValue =  new ArrayList<>();

			String dateheader = cs.getDateDetails().getFullDate().getName();
			
			// Add Header Value for Original Date
			headerValue.add(new HeaderValue(
					DataCrawlerService.ORIGINAL + dateheader, 
					origDate));
			System.out.println(DataCrawlerService.ORIGINAL + dateheader + " --- " + origDate);
			
			String modDate = DateMatcherService.getDate(origDate, cs.getDateDetails().getDatePattern(), config.getStandardDate());
			
			// Add Header Value of Modifed Date
			headerValue.add(new HeaderValue(
					dateheader, 
					modDate));
			System.out.println(dateheader + " --- " + modDate);
			
			headerValue.addAll(dataCrawlerService.loadBasicDetails(cs.getBasicDetails(), w));
			
			System.out.println("===============================");
			headerValueList.add(headerValue);
		}
		
		System.out.println("unprocessed data: " + headerValueList.size());
		
		if (headerValueList.size() != 0) {
			dataCrawlerService.setCrawledData(headerValueList);
		}
	}

}
