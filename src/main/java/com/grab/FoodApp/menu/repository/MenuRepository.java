package com.grab.FoodApp.menu.repository;

import com.grab.FoodApp.menu.entity.Menu;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {
    boolean existsByName(@NotBlank(message = "Menu name is required") String name);
}
