package com.grab.FoodApp.review.services;

import com.grab.FoodApp.auth_users.entity.User;
import com.grab.FoodApp.auth_users.services.UserService;
import com.grab.FoodApp.enums.OrderStatus;
import com.grab.FoodApp.exceptions.BadRequestException;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.menu.entity.Menu;
import com.grab.FoodApp.menu.repository.MenuRepository;
import com.grab.FoodApp.order.entity.Order;
import com.grab.FoodApp.order.repository.OrderItemRepository;
import com.grab.FoodApp.order.repository.OrderRepository;
import com.grab.FoodApp.response.Response;
import com.grab.FoodApp.review.dtos.ReviewDTO;
import com.grab.FoodApp.review.entity.Review;
import com.grab.FoodApp.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;


    @Override
    @Transactional
    public Response<ReviewDTO> createReview(ReviewDTO reviewDTO) {

        log.info("Inside createReview()");

        // Get current user
        User user = userService.getCurrentLoggedInUser();

        // Validate required fields
        if (reviewDTO.getOrderId() == null || reviewDTO.getMenuId() == null) {
            throw new BadRequestException("Order ID and Menu Item ID are required");
        }

        // Validate menu item exists
        Menu menu = menuRepository.findById(reviewDTO.getMenuId())
                .orElseThrow(() -> new NotFoundException("Menu item not found"));


        // Validate order exists
        Order order = orderRepository.findById(reviewDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        //make sure the order belongs to you
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This order doesn't belong to you");
        }

        // Validate order status is DELIVERED
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("You can only review items that has been delivered to you");
        }

        // Validate that menu item was part of this order
        boolean itemInOrder = orderItemRepository.existsByOrderIdAndMenuId(
                reviewDTO.getOrderId(),
                reviewDTO.getMenuId());

        if (!itemInOrder) {
            throw new BadRequestException("This menu item was not part of the specified order");
        }

        // Check if user already wrote a review for the item
        if (reviewRepository.existsByUserIdAndMenuIdAndOrderId(
                user.getId(),
                reviewDTO.getMenuId(),
                reviewDTO.getOrderId())) {
            throw new BadRequestException("You've already reviewed this item from this order");
        }

        // Create and save review
        Review review = Review.builder()
                .user(user)
                .menu(menu)
                .orderId(reviewDTO.getOrderId())
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Return response with review data
        ReviewDTO responseDto = modelMapper.map(savedReview, ReviewDTO.class);
        responseDto.setUserName(user.getName());
        responseDto.setMenuName(menu.getName());

        return Response.<ReviewDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Review added successfully")
                .data(responseDto)
                .build();

    }

    @Override
    public Response<List<ReviewDTO>> getReviewsForMenu(Long menuId) {
        log.info("Inside getReviewsForMenu()");

        List<Review> reviews = reviewRepository.findByMenuIdOrderByIdDesc(menuId);

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .toList();

        return Response.<List<ReviewDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Reviews retrieved successfully")
                .data(reviewDTOs)
                .build();

    }

    @Override
    public Response<Double> getAverageRating(Long menuId) {
        log.info("Inside getAverageRating()");

        Double averageRating = reviewRepository.calculateAverageRatingByMenuId(menuId);

        return Response.<Double>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Average rating retrieved successfully")
                .data(averageRating != null ? averageRating : 0.0)
                .build();
    }
}
