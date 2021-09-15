package com.raissapp.estore.PaymentsService.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Data
@Entity
@Table(name = "payments")
public class PaymentEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2033598295401861538L;

	@Id
    private String paymentId;

    @Column
    public String orderId;
	
}
