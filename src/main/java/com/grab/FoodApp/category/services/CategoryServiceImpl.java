package com.grab.FoodApp.category.services;

import com.grab.FoodApp.category.dtos.CategoryDTO;
import com.grab.FoodApp.category.entity.Category;
import com.grab.FoodApp.category.repository.CategoryRepository;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public Response<CategoryDTO> addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        CategoryDTO savedCategory = modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
        return Response.<CategoryDTO>builder()
                .message("Category added successfully")
                .statusCode(HttpStatus.OK.value())
                .data(savedCategory)
                .build();
    }

    @Override
    public Response<CategoryDTO> updateCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(categoryDTO.getId()).
                orElseThrow(() -> new NotFoundException("Category not found"));

        if(categoryDTO.getName() != null && !categoryDTO.getName().isEmpty()) {
            if(categoryRepository.existsByName(categoryDTO.getName()) &&
                    !existingCategory.getName().equals(categoryDTO.getName())) {
                throw new IllegalArgumentException("Category name already exists");
            }
        }
        existingCategory.setDescription(categoryDTO.getDescription());
        categoryRepository.save(existingCategory);

        CategoryDTO updatedCategoryDTO = modelMapper.map(existingCategory, CategoryDTO.class);
        return Response.<CategoryDTO>builder()
                .data(updatedCategoryDTO)
                .message("Category updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<CategoryDTO> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        return Response.<CategoryDTO>builder()
                .data(categoryDTO)
                .message("Category retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        return Response.<List<CategoryDTO>>builder()
                .data(categoryDTOs)
                .message("Categories retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<?> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
        return Response.builder()
                .message("Category deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }
}
