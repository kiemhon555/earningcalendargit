package com.euroland.earningcalendar.util.db;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.CrawlingResult;
import com.euroland.earningcalendar.domain.model.HeaderValue;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.euroland.earningcalendar.util.logger.LoggerHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

		Iterator<List<HeaderValue>> nds = newData.iterator();
		while (nds.hasNext()) {
			List<HeaderValue> nhv = nds.next();
			
			boolean addToDb = true;
			Iterator<List<HeaderValue>> its = dbData.iterator();
			while (its.hasNext()) {
				boolean testCheck = true;
				
				Iterator<HeaderValue> it = its.next().iterator();
				while (it.hasNext()) {
					HeaderValue hv = it.next();
					if (testCheck)
						testCheck = recordExist(nhv, hv.getHeader(), hv.getValue());
				}

				if (testCheck) {
					addToDb = false;
					
					// Remove Existing on Arrays
					its.remove();
					nds.remove();
				}
			}

			if (addToDb)
				ret.add(nhv);
		}
		
		dbDataUpdate = toBeRemovedDbData(dbData);

		return ret;
	}

	private List<List<HeaderValue>> toBeRemovedDbData(List<List<HeaderValue>> dbData) {
		
		// prepare db data to be removed
		Iterator<List<HeaderValue>> lhv = dbData.iterator();
		
		while (lhv.hasNext()) {
			
			HeaderValue date = lhv.next().stream()
					.filter(e -> e.getHeader().equals("Date")).findAny().orElse(null);
			if(date != null) {
				if(!date.getValue().equals("")) {
					LocalDate ld = LocalDate.parse(date.getValue(), DateTimeFormatter.ofPattern(standardDate));
					if (ld.isBefore(LocalDate.now())) {
						// Remove Data that is on the past
						lhv.remove();
					}
				}
			}
		}
		
		return dbData;
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
	
		List<String> ls= new ArrayList<>();
		
		llhv.stream().forEach( lhv -> {
			ls.add(new Gson().toJson(lhv));
		});
		
		ls.stream().distinct().collect(Collectors.toList()).forEach( s -> {
			result.add(new Gson().fromJson(s, new TypeToken<List<HeaderValue>>() {}.getType()));
		});
		
		logger.info("Distinct Data: " + result.size());
		
		return result;
	}
}
