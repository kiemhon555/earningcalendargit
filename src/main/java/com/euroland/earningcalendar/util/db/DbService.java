package com.euroland.earningcalendar.util.db;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.CrawlingResult;
import com.euroland.earningcalendar.domain.model.HeaderValue;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.euroland.earningcalendar.util.logger.LoggerHandler;

@Service
public class DbService {

	@Autowired
	ConfService confService;
	
	@Autowired
	LoggerHandler logger;
	
	private String standardDate;
	
	private List<List<HeaderValue>> dbDataUpdate = new ArrayList<>();
	
	public List<List<HeaderValue>> checkDuplicate(int sourceId, List<List<HeaderValue>> headerValue) {
		List<List<HeaderValue>> result = null;
		
		List<List<HeaderValue>> dbData = getPreviousDataFromDB(sourceId);

		if(dbData != null) {
			logger.info("Previous Data: " + dbData.size());
			result = distinctData(dbData, getDistinctNewData(headerValue));
			
		} else {
			result = getDistinctNewData(headerValue);
		}

		return result;

	}
	
	public void setStandardDate(String date) {
		standardDate = date;
	}
	
	public List<List<HeaderValue>> getDbDataUpdate() {
		return dbDataUpdate;
	}
	
	private List<List<HeaderValue>> distinctData(List<List<HeaderValue>> dbData, List<List<HeaderValue>> newData) {

		dbDataUpdate.clear();
		
		List<List<HeaderValue>> ret = new ArrayList<>();

		newData.stream().forEach(nd -> {
			boolean exist = dbData.removeIf(dd -> dd.stream().allMatch(hv -> 
					recordExist(nd, hv.getHeader(), hv.getValue())));
			
			// if data not exist on database
			if (!exist)
				ret.add(nd);
		});
		
		dbDataUpdate = toBeRemovedDbData(dbData);

		return ret;
	}

	private List<List<HeaderValue>> toBeRemovedDbData(List<List<HeaderValue>> dbData) {
		
		List<List<HeaderValue>> ret = new ArrayList<>();
		dbData.stream().forEach(dd -> {
			HeaderValue date = dd.stream().filter(e -> e.getHeader().equals("Date")).findAny().orElse(null);
			if(date != null) {
				if(!date.getValue().equals("")) {
					LocalDate ld = LocalDate.parse(date.getValue(), DateTimeFormatter.ofPattern(standardDate));
					// add data if not before todays date
					if (!ld.isBefore(LocalDate.now()))
						ret.add(dd);
				}
			}
		});
		
		return ret;
	}
	
	private boolean recordExist(List<HeaderValue> rlst, String header, String value) {
		Predicate<HeaderValue> p1 = p -> p.getHeader().equals(header) && p.getValue().equals(value);
		return rlst.stream().anyMatch(p1);
	}
	
	private List<List<HeaderValue>> getPreviousDataFromDB (int sourceId) {
		List<List<HeaderValue>> ret = null;
		
		// getting previous data from db
		CrawlingResult hvd = (CrawlingResult) confService.prepareTestConf(
				confService.HOST + confService.PREV_CRAWLED_DATA_LINK + Integer.toString(sourceId), new CrawlingResult());
		
		if (hvd != null) {
			ret = hvd.getResults();
		}
		
		return ret;
	}
	
	private List<List<HeaderValue>> getDistinctNewData(List<List<HeaderValue>> llhv) {
		List<List<HeaderValue>> result = new ArrayList<>();
		List<String> temp = new ArrayList<String>();
		
		llhv.stream().forEach(l -> {
			if (!temp.contains(l.toString())) {
				temp.add(l.toString());
				result.add(l);
			}
		});
		
		logger.info("Distinct Data: " + result.size());
		
		return result;
	}
}
