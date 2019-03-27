package com.euroland.earningcalendar.rabbit;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.domain.model.CrawlingResult;
import com.euroland.earningcalendar.util.configuration.ConfService;

@Service
public class Producer {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ConfService confService;

	public boolean produce(CrawlingResult crawlingResult) {
		
		MessagePostProcessor mpp = new MessagePostProcessor() {
			@Override
			public org.springframework.amqp.core.Message postProcessMessage(
					org.springframework.amqp.core.Message message) throws AmqpException {
				message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				return message;
			}
		};

		try {
			rabbitTemplate.convertAndSend(confService.RABBIT_EXCHANGE, confService.RABBIT_ROUTING_KEY, crawlingResult, mpp);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public Message postProcessMessage(Message message) throws AmqpException {
		message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		return message;
	}

}
