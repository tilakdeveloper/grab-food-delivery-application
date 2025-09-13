package com.grab.FoodApp.payment.controllers;

import com.grab.FoodApp.payment.dtos.PaymentDTO;
import com.grab.FoodApp.payment.services.PaymentService;
import com.grab.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Response<?>> initializePayment(@RequestBody @Valid PaymentDTO paymentRequest){
        return ResponseEntity.ok(paymentService.initializePayment(paymentRequest));
    }

    @PutMapping("/update")
    public void updateOrderAfterPayment(@RequestBody PaymentDTO paymentRequest){
        paymentService.updatePaymentForOrder(paymentRequest);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<PaymentDTO>>> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Response<PaymentDTO>> getPaymentById(@PathVariable Long paymentId){
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}
