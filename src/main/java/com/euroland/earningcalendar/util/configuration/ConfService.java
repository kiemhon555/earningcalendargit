package com.euroland.earningcalendar.util.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.stereotype.Service;

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
	public Object prepareTestConf(String file, Object obj) {
//		System.out.append(file);
		String json = null;
		try {
			json = getStrFromFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
