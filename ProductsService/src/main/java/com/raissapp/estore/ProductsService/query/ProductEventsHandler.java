package com.raissapp.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.raissapp.estore.ProductsService.core.data.ProductEntity;
import com.raissapp.estore.ProductsService.core.data.ProductRepository;
import com.raissapp.estore.ProductsService.core.events.ProductCreatedEvent;
import com.raissapp.estore.core.events.ProductReservationCanceledEvent;
import com.raissapp.estore.core.events.ProductReservedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	
	private final ProductRepository productRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);
	
	@Autowired
	public ProductEventsHandler(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	 
	@ExceptionHandler(resultType = Exception.class)
	public void handle(Exception exception) throws Exception {

		throw exception;
	}
	
	@ExceptionHandler(resultType = IllegalArgumentException.class)
	public void handle(IllegalArgumentException exception) {
		//Log error message
		
	}
	
	@EventHandler
	public void on(ProductCreatedEvent event) throws Exception {
		
		ProductEntity productEntity = new ProductEntity();
		BeanUtils.copyProperties(event, productEntity);
		try {
			productRepository.save(productEntity);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		//if(true) throw new Exception("forcing exception in the Event Handler class");

	}
	
	@EventHandler
	public void on(ProductReservationCanceledEvent productReservationCanceledEvent) {
		
		ProductEntity currentlyStoredProduct  = productRepository.findByProductId(productReservationCanceledEvent.getProductId());
		
		LOGGER.debug("ProductReservationCanceledEvent: Current product quantity "+ currentlyStoredProduct.getQuantity());
		
		int newQuantity = currentlyStoredProduct.getQuantity() + productReservationCanceledEvent.getQuantity();
		
		currentlyStoredProduct.setQuantity(newQuantity);
		
		productRepository.save(currentlyStoredProduct);
		
		LOGGER.debug("ProductReservationCanceledEvent: New product quantity "+ currentlyStoredProduct.getQuantity());

	}
	
	@EventHandler
	public void on(ProductReservedEvent productReservedEvent) {
		
		ProductEntity productEntity = productRepository.findByProductId(productReservedEvent.getProductId());
		
		
		LOGGER.debug("ProductReservedEvent: Current product quantity "+ productEntity.getQuantity());
		
		productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
		
		
		productRepository.save(productEntity);
		
		LOGGER.debug("ProductReservedEvent: New product quantity "+ productEntity.getQuantity());

		LOGGER.info("ProductReservedEvent is called for productId: "+productReservedEvent.getProductId()+" and orderId: "+ productReservedEvent.getOrderId());
		
	}
	
}
