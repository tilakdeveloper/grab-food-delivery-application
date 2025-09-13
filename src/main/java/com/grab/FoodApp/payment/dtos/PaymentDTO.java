package com.grab.FoodApp.payment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.grab.FoodApp.auth_users.dtos.UserDTO;
import com.grab.FoodApp.enums.PaymentGateway;
import com.grab.FoodApp.order.dtos.OrderDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {

    private Long id;

    private Long orderId; // Associated order ID

    private BigDecimal amount; // Payment amount

    private String paymentStatus; // e.g., Completed, Pending, Failed

    private String transactionId;

    private PaymentGateway paymentGateway;

    private String failureReason; // Reason for failure if any

    private boolean success; // Indicates if the payment was successful

    private LocalDateTime localDateTime;

    private OrderDTO order;

    private UserDTO user;

}
