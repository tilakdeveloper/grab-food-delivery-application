package com.grab.FoodApp.category.services;


import com.grab.FoodApp.category.dtos.CategoryDTO;
import com.grab.FoodApp.response.Response;

import java.util.List;

public interface CategoryService {

    Response<CategoryDTO> addCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> updateCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> getCategoryById(Long id);

    Response<List<CategoryDTO>> getAllCategories();

    Response<?> deleteCategory(Long id);
}
