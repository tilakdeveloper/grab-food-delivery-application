package com.grab.FoodApp.payment.repository;

import com.grab.FoodApp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
