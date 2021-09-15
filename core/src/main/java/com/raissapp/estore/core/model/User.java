package com.raissapp.estore.core.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {

	private final String firstName;
	private final String lastName;
	private final String userId;
	private final PaymentDetails paymentDetails;
	
}
