package com.grab.FoodApp.category.controllers;

import com.grab.FoodApp.category.dtos.CategoryDTO;
import com.grab.FoodApp.category.services.CategoryService;
import com.grab.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<CategoryDTO>> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        Response<CategoryDTO> response = categoryService.addCategory(categoryDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<CategoryDTO>> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        Response<CategoryDTO> response = categoryService.updateCategory(categoryDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        Response<CategoryDTO> response = categoryService.getCategoryById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<CategoryDTO>>> getAllCategories() {
        Response<List<CategoryDTO>> response = categoryService.getAllCategories();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> deleteCategory(@PathVariable Long id) {
        Response<?> response = categoryService.deleteCategory(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
