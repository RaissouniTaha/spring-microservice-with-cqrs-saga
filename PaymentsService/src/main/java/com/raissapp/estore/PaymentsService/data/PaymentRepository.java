package com.raissapp.estore.PaymentsService.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity,  String> {

}
