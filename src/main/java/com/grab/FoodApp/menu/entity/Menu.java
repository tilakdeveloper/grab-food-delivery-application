package com.grab.FoodApp.menu.entity;

import com.grab.FoodApp.category.entity.Category;
import com.grab.FoodApp.order.entity.OrderItem;
import com.grab.FoodApp.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private  String description;
    private BigDecimal price;    // BigDecimal for currency representation, make maths operations easier and more precise
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")  // Link to Category entity
    private Category category;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
