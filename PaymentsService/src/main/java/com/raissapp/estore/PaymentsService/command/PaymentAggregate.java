package com.raissapp.estore.PaymentsService.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.raissapp.estore.core.command.ProcessPaymentCommand;
import com.raissapp.estore.core.events.PaymentProcessedEvent;
import com.raissapp.estore.core.model.PaymentDetails;

@Aggregate
public class PaymentAggregate {

	@AggregateIdentifier
	private  String paymentId;
	private  String orderId;
	
	public PaymentAggregate() {
		
	}
	
	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		
		if(processPaymentCommand.getPaymentDetails() == null ) {
			throw new IllegalArgumentException("Missing payment details");
		}
		if(processPaymentCommand.getOrderId() == null ) {
			throw new IllegalArgumentException("Missing orderId");
		}
		if(processPaymentCommand.getPaymentId() == null ) {
			throw new IllegalArgumentException("Missing paymentId");
		}
		
		PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
				.orderId(processPaymentCommand.getOrderId())
				.paymentId(processPaymentCommand.getPaymentId())
				.build();
		AggregateLifecycle.apply(paymentProcessedEvent);
	}
	
	@EventSourcingHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) throws Exception{
		this.orderId = paymentProcessedEvent.getOrderId();
		this.paymentId = paymentProcessedEvent.getPaymentId();
	}
}
