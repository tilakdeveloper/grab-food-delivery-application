package com.grab.FoodApp.menu.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.grab.FoodApp.review.dtos.ReviewDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuDTO {

    private Long id;

    @NotBlank(message = "Menu name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private MultipartFile imageFile;

    private List<ReviewDTO> reviews;
}
