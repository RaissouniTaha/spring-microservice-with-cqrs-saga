package com.raissapp.estore.OrdersService.core.events;

import com.raissapp.estore.OrdersService.core.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class OrderCreatedEvent {
	private  String orderId;
	private  String userId;
	private  String productId;
	private  int quantity;
	private  String addressId;
	private  OrderStatus orderStatus;
}
