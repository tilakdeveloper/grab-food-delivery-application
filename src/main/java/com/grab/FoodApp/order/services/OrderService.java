package com.grab.FoodApp.order.services;

import com.grab.FoodApp.enums.OrderStatus;
import com.grab.FoodApp.order.dtos.OrderDTO;
import com.grab.FoodApp.order.dtos.OrderItemDTO;
import com.grab.FoodApp.response.Response;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Response<?> placeOrderFromCart();

    Response<OrderDTO> getOrderById(Long id);

    Response<Page<OrderDTO>> getAllOrders(OrderStatus orderStatus, int page, int size);

    Response<List<OrderDTO>> getOrdersOfUser();

    Response<OrderItemDTO> getOrderItemById(Long orderItemId);

    Response<OrderDTO> updateOrderStatus(OrderDTO orderDTO);

    Response<Long> countUniqueCustomers();
}
