package com.grab.FoodApp.cart.services;

import com.grab.FoodApp.auth_users.entity.User;
import com.grab.FoodApp.auth_users.repository.UserRepository;
import com.grab.FoodApp.auth_users.services.UserService;
import com.grab.FoodApp.cart.dtos.CartDTO;
import com.grab.FoodApp.cart.entity.Cart;
import com.grab.FoodApp.cart.entity.CartItem;
import com.grab.FoodApp.cart.repository.CartItemRepository;
import com.grab.FoodApp.cart.repository.CartRepository;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.menu.entity.Menu;
import com.grab.FoodApp.menu.repository.MenuRepository;
import com.grab.FoodApp.response.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public Response<?> addItemToCart(CartDTO cartDTO) {
        Long menuId = cartDTO.getMenuId();
        int quantity = cartDTO.getQuantity();

        User user = userService.getCurrentLoggedInUser();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu Item Not Found"));

        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        //If item already in cart
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenu().getId().equals(menuId))
                .findFirst();

        if(optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        } else {
            // if cart item is not present
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .pricePerUnit(menu.getPrice())
                    .subTotal(menu.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();

            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        // cartRepository.save(cart)   // will auto persist

        CartDTO savedCartDTO = modelMapper.map(cart, CartDTO.class);

        return Response.<CartDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Item added to cart successfully")
                .data(savedCartDTO)
                .build();
    }

    @Override
    public Response<?> incrementItem(Long menuId) {
        User user = userService.getCurrentLoggedInUser();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        int newQuantity = cartItem.getQuantity() + 1;
        cartItem.setQuantity(newQuantity);

        cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));

        cartItemRepository.save(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity incremented successfully")
                .build();
    }

    @Override
    public Response<?> decrementItem(Long menuId) {
        User user = userService.getCurrentLoggedInUser();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getMenu().getId().equals(menuId))
                .findFirst().orElseThrow(() -> new NotFoundException("Menu not found in cart"));

        if (cartItem.getQuantity() <= 1) {
            // Remove item if quantity becomes 0 or less
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            int newQuantity = cartItem.getQuantity() - 1;
            cartItem.setQuantity(newQuantity);
            cartItem.setSubTotal(cartItem.getPricePerUnit().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(cartItem);
        }

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item quantity decremented successfully")
                .build();
    }

    @Override
    public Response<?> removeItem(Long cartItemId) {
        User user = userService.getCurrentLoggedInUser();
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Item removed from cart successfully")
                .build();
    }

    @Override
    public Response<CartDTO> getShoppingCart() {
        User user = userService.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                totalAmount = totalAmount.add(item.getSubTotal());
            }
        }

        cartDTO.setTotalAmount(totalAmount);

        return Response.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart retrieved successfully")
                .data(cartDTO)
                .build();
    }

    @Override
    public Response<?> clearShoppingCart() {
        User user = userService.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));

        // Remove all cart items
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cartRepository.save(cart);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Shopping cart cleared successfully")
                .build();
    }
}
