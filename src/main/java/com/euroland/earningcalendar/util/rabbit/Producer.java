package com.euroland.earningcalendar.util.rabbit;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.euroland.earningcalendar.model.CrawlingResult;

@Service
public class Producer {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final String EXCHANGE = "crawler.exchange";
	private static final String ROUTING_KEY = "crawler_routing_key";

	public void produce(CrawlingResult crawlingResult) {
		
		MessagePostProcessor mpp = new MessagePostProcessor() {
			@Override
			public org.springframework.amqp.core.Message postProcessMessage(
					org.springframework.amqp.core.Message message) throws AmqpException {
				message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				return message;
			}
		};

		rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, crawlingResult, mpp);
	}

	public Message postProcessMessage(Message message) throws AmqpException {
		message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		return message;
	}

}
