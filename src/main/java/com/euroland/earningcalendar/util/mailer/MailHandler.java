package com.euroland.earningcalendar.util.mailer;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.euroland.earningcalendar.model.mail.MailObject;
import com.euroland.earningcalendar.util.configuration.ConfService;
import com.euroland.earningcalendar.util.logger.LoggerHandler;
import com.euroland.earningcalendar.util.thread.ThreadHandler;

@Service
public class MailHandler {

	@Autowired
	ConfService confService;
	
	@Autowired
	LoggerHandler logger;
	
	public void sendMail(String message){
		boolean status = false;
		int ctr = 1;
		while(!status && ctr < 4) {
			try {
				if (!message.equals("")) {
					RestTemplate restTemplate = new RestTemplate();
					final String baseUrl = confService.MAILER_HOST;
					URI uri;
						uri = new URI(baseUrl);
		
					HttpHeaders headers = new HttpHeaders();
					headers.set("X-COM-LOCATION", "USA");
					headers.set("Content-type", "application/json");
					HttpEntity<MailObject> request = new HttpEntity<>(
							new MailObject( confService.MAILER_TO, confService.MAILER_CC, 
									"Crawler Notification", message), headers);
					ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
					logger.info(result.getBody());
				}
				else {
					//no message
				}
				status = true;
			} catch (Exception e) {
				// Fail Sending Email
				logger.error("Failed to send mail(Retry: " + ctr + "): " + message);
				ThreadHandler.sleep(10000);
				ctr++;
			}
		}
	}
}
