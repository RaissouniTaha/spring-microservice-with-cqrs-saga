package com.raissapp.estore.ProductsService.command.rest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raissapp.estore.ProductsService.command.CreateProductCommand;

@RestController
@RequestMapping("/products")  // the entry point  ==>  http://localhost:8080/products
public class ProductsCommandController {

	private final Environment env;
	private final CommandGateway commandGateway;
	
	@Autowired
	public ProductsCommandController(Environment env, CommandGateway commandGateway) {
		this.env = env;
		this.commandGateway = commandGateway;
	}

	@PostMapping
	public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
													.price(createProductRestModel.getPrice())
													.quantity(createProductRestModel.getQuantity())
													.title(createProductRestModel.getTitle())
													.productId(UUID.randomUUID().toString()).build();
		String returnedValue;
		
		returnedValue = commandGateway.sendAndWait(createProductCommand);

//		try {
//			returnedValue = commandGateway.sendAndWait(createProductCommand);
//		}catch (Exception e) {
//			returnedValue = e.getLocalizedMessage();
//		} 
		return returnedValue;
	}
	
//	@GetMapping
//	public String getProduct() {
//		return "HTTP GET Handled "+ env.getProperty("local.server.port");
//	}
}
