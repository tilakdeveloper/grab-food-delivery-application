package com.grab.FoodApp.category.repository;

import com.grab.FoodApp.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(@NotBlank(message = "Category name is required") String name);
}
