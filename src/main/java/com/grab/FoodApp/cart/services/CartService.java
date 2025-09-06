package com.grab.FoodApp.cart.services;

import com.grab.FoodApp.cart.dtos.CartDTO;
import com.grab.FoodApp.response.Response;

public interface CartService {

    Response<?> addItemToCart(CartDTO cartDTO);

    Response<?> incrementItem(Long menuId);

    Response<?> decrementItem(Long menuId);

    Response<?> removeItem(Long cartItemId);

    Response<CartDTO> getShoppingCart();

    Response<?> clearShoppingCart();
}
