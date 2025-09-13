package com.grab.FoodApp.payment.services;

import com.grab.FoodApp.payment.dtos.PaymentDTO;
import com.grab.FoodApp.response.Response;

import java.util.List;

public interface PaymentService {

    Response<?> initializePayment(PaymentDTO paymentDTO);

    void updatePaymentForOrder(PaymentDTO paymentDTO);

    Response<List<PaymentDTO>> getAllPayments();

    Response<PaymentDTO> getPaymentById(Long paymentId);

}
