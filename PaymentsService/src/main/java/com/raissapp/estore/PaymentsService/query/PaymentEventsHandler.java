package com.raissapp.estore.PaymentsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raissapp.estore.PaymentsService.data.PaymentEntity;
import com.raissapp.estore.PaymentsService.data.PaymentRepository;
import com.raissapp.estore.core.events.PaymentProcessedEvent;

@Component
public class PaymentEventsHandler {

	 private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);
	 private final PaymentRepository paymentRepository;
	
	 @Autowired
	 public PaymentEventsHandler(PaymentRepository paymentRepository) {
	        this.paymentRepository = paymentRepository;
	 }
	 
	 @EventHandler
	 public void on(PaymentProcessedEvent paymentProcessedEvent) {
		 LOGGER.info("PaymentProcessedEvent is called for orderdId: "+ paymentProcessedEvent.getOrderId());
		 
		 PaymentEntity paymentEntity = new PaymentEntity();
		 BeanUtils.copyProperties(paymentProcessedEvent, paymentEntity);
		 paymentRepository.save(paymentEntity);
	 }
}
