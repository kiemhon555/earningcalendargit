package com.euroland.earningcalendar.util.db;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.CrawlingResult;
import com.euroland.earningcalendar.domain.model.HeaderValue;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class DbService {

	@Autowired
	ConfService confService;
	
	private static final Logger logger = LoggerFactory.getLogger(DbService.class);
	
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
	
	private List<List<HeaderValue>> distinctData(List<List<HeaderValue>> dbData, List<List<HeaderValue>> newData) {

		List<List<HeaderValue>> ret = new ArrayList<>();
		for (List<HeaderValue> lio : newData) {

			boolean addToDb = true;
			for (List<HeaderValue> r1 : dbData) {
//				System.out.println(r1);
				boolean testCheck = true;
				for (HeaderValue hv : r1) {
					if (testCheck) {
						testCheck = recordExist(lio, hv.getHeader(), hv.getValue());
					}
				}
				if (testCheck) {
					addToDb = false;
				}
			}

			if (addToDb) {
				ret.add(lio);
			}
		}
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
