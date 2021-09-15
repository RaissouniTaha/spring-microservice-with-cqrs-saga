package com.raissapp.estore.OrdersService.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raissapp.estore.OrdersService.command.commands.CreateOrderCommand;
import com.raissapp.estore.OrdersService.core.model.OrderStatus;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

	
	private final CommandGateway commandGateway;
	
	@Autowired
	public OrdersCommandController(Environment env, CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}
	
	@PostMapping
	public String createOrder(@Valid @RequestBody CreateOrdersRestModel createOrdersRestModel) {
		
		String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
		
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
												.productId(createOrdersRestModel.getProductId())
												.quantity(createOrdersRestModel.getQuantity())
												.addressId(createOrdersRestModel.getAddressId())
												.userId(userId)
												.orderId(UUID.randomUUID().toString())
												.orderStatus(OrderStatus.CREATED)
												.build();
		String returnedValue = commandGateway.sendAndWait(createOrderCommand);
		
		return returnedValue;
	}
	
}
