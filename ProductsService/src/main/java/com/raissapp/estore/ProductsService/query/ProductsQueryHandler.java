package com.raissapp.estore.ProductsService.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raissapp.estore.ProductsService.core.data.ProductEntity;
import com.raissapp.estore.ProductsService.core.data.ProductRepository;
import com.raissapp.estore.ProductsService.query.rest.ProductRestModel;

@Component
public class ProductsQueryHandler {
	
	private final ProductRepository productRepository;
	
	@Autowired
	public ProductsQueryHandler(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery query){
		List<ProductRestModel> productsRest = new ArrayList<ProductRestModel>();
		List<ProductEntity> storedProducts =  productRepository.findAll();
		for(ProductEntity productEntity : storedProducts) {
			ProductRestModel productRestModel = new ProductRestModel();
			BeanUtils.copyProperties(productEntity, productRestModel);
			productsRest.add(productRestModel);
		}
		return productsRest;
	}
}
