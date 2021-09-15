package com.raissapp.estore.OrdersService.core.events;

import com.raissapp.estore.OrdersService.core.model.OrderStatus;

import lombok.Value;

@Value
public class OrderApprovedEvent {

	private final String orderId; 
	private final OrderStatus orderStatus = OrderStatus.APPROVED;
	
}
