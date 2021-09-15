package com.raissapp.estore.ProductsService.command.interceptors;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raissapp.estore.ProductsService.command.CreateProductCommand;
import com.raissapp.estore.ProductsService.core.data.ProductLookupEntity;
import com.raissapp.estore.ProductsService.core.data.ProductLookupRepository;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

	private final ProductLookupRepository productLookupRepository;
	
	@Autowired
	public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}

	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
			List<? extends CommandMessage<?>> messages) {
		// TODO Auto-generated method stub
		return (index, command) -> {
			
			LOGGER.info("Interceptor command: "+ command.getPayloadType());
			
			if(CreateProductCommand.class.equals(command.getPayloadType())) {
			
				CreateProductCommand createProductCommand = (CreateProductCommand)command.getPayload();
				
				ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
				
				if(productLookupEntity != null) {
					throw new IllegalStateException(
							String.format("Product with productId %s or title %s already exist",
									createProductCommand.getProductId(), createProductCommand.getTitle())
							);
				}
			}
			return command;
		};
	}

	
	
}
