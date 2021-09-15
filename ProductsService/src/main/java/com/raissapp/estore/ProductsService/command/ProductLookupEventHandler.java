package com.raissapp.estore.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raissapp.estore.ProductsService.core.data.ProductLookupEntity;
import com.raissapp.estore.ProductsService.core.data.ProductLookupRepository;
import com.raissapp.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventHandler {
	
	
	private final ProductLookupRepository productLookupRepository;
	
	@Autowired
	public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}


	@EventHandler
	public void on(ProductCreatedEvent event) {
		ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(),event.getTitle());
		productLookupRepository.save(productLookupEntity);
	}
	
}
