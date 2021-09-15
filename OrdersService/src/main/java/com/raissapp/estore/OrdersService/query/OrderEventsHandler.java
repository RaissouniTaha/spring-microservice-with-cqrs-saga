package com.raissapp.estore.OrdersService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raissapp.estore.OrdersService.core.data.OrderEntity;
import com.raissapp.estore.OrdersService.core.data.OrderRepository;
import com.raissapp.estore.OrdersService.core.events.OrderApprovedEvent;
import com.raissapp.estore.OrdersService.core.events.OrderCreatedEvent;
import com.raissapp.estore.OrdersService.core.events.OrderRejectedEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {

	private final OrderRepository orderRepository;
	
	@Autowired
	public OrderEventsHandler(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	@EventHandler
	public void on(OrderCreatedEvent event) throws Exception {
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(event, orderEntity);
		
		this.orderRepository.save(orderEntity);
	}
	
	@EventHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());
		if(orderEntity == null ) {
			//TODO: Do something about it
			return ;
		}
		
		orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
		orderRepository.save(orderEntity);
	}
	
	@EventHandler
	public void on(OrderRejectedEvent rejectedEvent) {
		OrderEntity orderEntity = orderRepository.findByOrderId(rejectedEvent.getOrderId());
		
		orderEntity.setOrderStatus(rejectedEvent.getOrderStatus());
		orderRepository.save(orderEntity);
	}
	
	
	
}
