package com.euroland.earningcalendar.util.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.EarningCalendarApplication;
import com.euroland.earningcalendar.model.HeaderValue;
import com.euroland.earningcalendar.model.HeaderValueData;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

@Service
public class DbService {

	@Autowired
	ConfService confService;
	
	public List<List<HeaderValue>> checkDuplicate(int sourceId, List<List<HeaderValue>> headerValue) {
		List<List<HeaderValue>> result = null;
		
		List<List<HeaderValue>> dbData = getPreviousDataFromDB(sourceId);
		
		if(dbData != null) {
			
			result = distinctData(dbData, headerValue);
			
		} else {
			
			result = headerValue;
			
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
						// System.out.println(hv.getHeader());
						// System.out.println(hv.getValue());
						testCheck = recordExist(lio, hv.getHeader(), hv.getValue());
						// System.out.println(testCheck);
					}
				}
				if (testCheck) {
//					System.out.println("this is duplicate" + r1.toString());
					addToDb = false;
				}
			}

			if (addToDb) {
//				System.out.println("this is not duplicate, add to db");
				System.out.println(lio.get(1).getValue() + " -- " + lio.get(2).getValue());
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
		
		HeaderValueData hvd = (HeaderValueData) confService.prepareTestConf(
				EarningCalendarApplication.HOST + "/find_header_value/" + Integer.toString(sourceId), new HeaderValueData());
		
		if (hvd != null) {
			ret = hvd.getHeader_value();
		}
		
		return ret;
	}
	
	private List<List<HeaderValue>> getNewConfig() {

		String json = "";
		try {
			json = getStrFromFile(".\\src\\main\\resources\\companies\\new.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<List<HeaderValue>> l = new Gson().fromJson(
				json, 
				new TypeToken<List<List<HeaderValue>>>() {}.getType());
		return l;
	}
	
	private List<List<HeaderValue>> getOldConfig() {

		String json = "";
		try {
			json = getStrFromFile(".\\src\\main\\resources\\companies\\old.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<List<HeaderValue>> l = new Gson().fromJson(
				json, 
				new TypeToken<List<List<HeaderValue>>>() {}.getType());
		return l;
	}
	
	private String getStrFromFile(String pathname) throws IOException {
		FileInputStream fis = new FileInputStream(pathname);
		StringBuilder sb = new StringBuilder();
		Reader r = new InputStreamReader(fis, "UTF-8");
		char[] buf = new char[1024];
		int amt = r.read(buf);
		while (amt > 0) {
			sb.append(buf, 0, amt);
			amt = r.read(buf);
		}
		fis.close();
		return sb.toString();
	}
}
