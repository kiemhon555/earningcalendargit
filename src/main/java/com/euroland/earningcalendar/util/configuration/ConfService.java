package com.euroland.earningcalendar.util.configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


@Service
public class ConfService {

	/**
	 * prepare Conf object from given json file
	 * 
	 * @param file
	 * @return 
	 * @return Conf class
	 * @throws IOException
	 */
	public Object prepareTestConf(String path, Object obj) {
//		System.out.append(file);
		String json = null;
		try {
			if(path.contains("http")) {
				json = readUrl(path);
			} else {
				json = getStrFromFile(path);
			}
		} catch (Exception e) {
			
			System.out.println("Failed Loading Config: " + obj.getClass());
			
			return null;
		}
		
		obj = new Gson().fromJson(json, obj.getClass());
		
		return obj;
	}
	
	/**
	 * read text file into string from given file+path
	 * 
	 * @param pathname
	 * @return String
	 * @throws IOException
	 */
	public String getStrFromFile(String pathname) throws IOException {
		FileInputStream fis = new FileInputStream(pathname);
		StringBuilder sb = new StringBuilder();
		Reader r = new InputStreamReader(fis, "Cp1252");
		char[] buf = new char[1024];
		int amt = r.read(buf);
		
		while (amt > 0) {
			sb.append(buf, 0, amt);
			amt = r.read(buf);
		}
		fis.close();
		return sb.toString();
	}
	
	public String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	        
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}

}
