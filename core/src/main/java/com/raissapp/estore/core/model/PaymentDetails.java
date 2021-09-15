package com.raissapp.estore.core.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentDetails {

	private final String name;
	private final String cardNumber;
	private final int validUntilMonth;
	private final int validUntilYear;
	private final String cvv;
	
}
