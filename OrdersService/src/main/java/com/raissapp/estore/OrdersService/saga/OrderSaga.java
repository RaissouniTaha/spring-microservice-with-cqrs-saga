package com.raissapp.estore.OrdersService.saga;


import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.raissapp.estore.OrdersService.command.commands.ApproveOrderCommand;
import com.raissapp.estore.OrdersService.command.commands.RejectOrderCommand;
import com.raissapp.estore.OrdersService.core.events.OrderApprovedEvent;
import com.raissapp.estore.OrdersService.core.events.OrderCreatedEvent;
import com.raissapp.estore.OrdersService.core.events.OrderRejectedEvent;
import com.raissapp.estore.core.command.CancelProductReservationCommand;
import com.raissapp.estore.core.command.ProcessPaymentCommand;
import com.raissapp.estore.core.command.ReserveProductCommand;
import com.raissapp.estore.core.events.PaymentProcessedEvent;
import com.raissapp.estore.core.events.ProductReservationCanceledEvent;
import com.raissapp.estore.core.events.ProductReservedEvent;
import com.raissapp.estore.core.model.User;
import com.raissapp.estore.core.query.FetchUserPaymentDetailsQuery;

@Saga
public class OrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway; 
	
	@Autowired
	private transient QueryGateway queryGateway;

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
	
	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity())
				.userId(orderCreatedEvent.getUserId())
				.build();
		LOGGER.info("OrderCreatedEvent handled for orderId: "+ reserveProductCommand.getOrderId() + " and productId: "+ reserveProductCommand.getProductId());
		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {

				if(commandResultMessage.isExceptional()) {
					
					//Start a compensation transaction
					
				}
				
			}
			
		});
	
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		//Process user payment
		LOGGER.info("ProductReservedEvent is called for productId: "+ productReservedEvent.getProductId()+" and orderId: "+productReservedEvent.getOrderId());
		
		FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
		User userPaymentDetails = null;
		try {
			userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		}catch (Exception e) {
			LOGGER.error(e.getMessage());
			
			//start compensation transaction
			cancelProductReservation(productReservedEvent, e.getMessage());
			return;
		}
		
		if(userPaymentDetails == null) {
			
			//start compensating transaction
			
			cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
			
			return;
		}
		
		LOGGER.info("Successfully fetched user payment details for user "+userPaymentDetails.getFirstName());
	
		ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.paymentDetails(userPaymentDetails.getPaymentDetails())
				.paymentId(UUID.randomUUID().toString())
				.build();
		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
		}catch (Exception e) {
			LOGGER.error(e.getMessage());
			//start compensation transaction
			cancelProductReservation(productReservedEvent, e.getMessage());
			return ;
		}
		
		if(result == null) {
			LOGGER.info("The ProcessPaymentCommand resulted in Null. Initiating a compensating transaction");
			//start compensation transaction
			cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");

		}
	}
	
	
	private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
		
		CancelProductReservationCommand publishProductReservationCommand = 
				CancelProductReservationCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.productId(productReservedEvent.getProductId())
				.quantity(productReservedEvent.getQuantity())
				.userId(productReservedEvent.getUserId())
				.reason(reason)
				.build();
		commandGateway.send(publishProductReservationCommand);
		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		
		//Send an ApproveOrderCommand
		
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
		commandGateway.send(approveOrderCommand);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent ) {
		
		LOGGER.info("Order is approved. Order Saga is complete for orderId: "+ orderApprovedEvent.getOrderId());
		
		//SagaLifecycle.end();
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCanceledEvent productReservationCanceledEvent) {
		//Create and send a RejectOrderCommand
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCanceledEvent.getOrderId(), productReservationCanceledEvent.getReason());
		
		commandGateway.send(rejectOrderCommand);
	
	}
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		
		LOGGER.info("Successfully rejected order with id "+ orderRejectedEvent.getOrderId());
		
	}
	
}
