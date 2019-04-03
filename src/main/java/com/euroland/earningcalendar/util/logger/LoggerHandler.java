package com.euroland.earningcalendar.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggerHandler {

	private String cname = "";
	
	public void info(String m) {
		Logger logger = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		logger.info(cname + m);
	}
	
	public void error(String m) {
		Logger logger = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		logger.error(cname + m);
	}

	public void debug(String m) {
		Logger logger = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		logger.debug(cname + m);
	}
		
	public void setCname(String cname) {
		if(!cname.equals("")) 
			this.cname = "[" + cname + "] ";
	}
}
