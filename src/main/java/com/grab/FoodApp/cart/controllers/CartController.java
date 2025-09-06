package com.grab.FoodApp.cart.controllers;

import com.grab.FoodApp.cart.dtos.CartDTO;
import com.grab.FoodApp.cart.services.CartService;
import com.grab.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Response<?>> addItemToCart(@RequestBody CartDTO cartDTO) {
        Response<?> response = cartService.addItemToCart(cartDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/items/increment/{menuId}")
    public ResponseEntity<Response<?>> incrementItem(@PathVariable Long menuId) {
        Response<?> response = cartService.incrementItem(menuId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/items/decrement/{menuId}")
    public ResponseEntity<Response<?>> decrementItem(@PathVariable Long menuId) {
        Response<?> response = cartService.decrementItem(menuId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Response<?>> removeItem(@PathVariable Long cartItemId) {
        Response<?> response = cartService.removeItem(cartItemId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<CartDTO>> getShoppingCart() {
        Response<CartDTO> response = cartService.getShoppingCart();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Response<?>> clearShoppingCart() {
        Response<?> response = cartService.clearShoppingCart();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
