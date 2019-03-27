package com.euroland.earningcalendar.selenium;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConnectionManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	
	boolean ConnectOnlyWhenOnline() {
		int tries = 0;
		int connectionWait = 2000;

		boolean tryConnection = false;
		tryConnection = ConnectionExists();

		if (tryConnection == false) {
			while (!tryConnection) {
				logger.error("ConnectOnlyWhenOnline is not available loop");
				
				tryConnection = ConnectionExists();
				try {
					Thread.sleep(connectionWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tries++;
				if (tries > 100) {
					logger.error("ConnectOnlyWhenOnline is not available loop BIG wait");
					connectionWait = 15000;
				}
			}
		}
		return true;
	}

	private boolean ConnectionExists() {
		try {
			final URL url = getRelaySite();
			final URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private URL getRelaySite() throws MalformedURLException {
		List<URL> doms = Arrays.asList(new URL("http://www.google.com"), new URL("http://www.yahoo.com"),
				new URL("http://www.amazon.com"), new URL("https://www.wikipedia.org"),
				new URL("https://www.reddit.com"), new URL("https://twitter.com"), new URL("https://www.instagram.com"),
				new URL("https://www.linkedin.com"), new URL("https://www.msn.com"), new URL("https://www.blogger.com"),
				new URL("https://www.apple.com"), new URL("http://www.imdb.com"), new URL("https://www.craigslist.com"),
				new URL("https://www.ebay.com"));
		return doms.get(randInt(0, 13));
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

}
