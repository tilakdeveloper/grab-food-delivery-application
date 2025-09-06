package com.grab.FoodApp.menu.services;

import com.grab.FoodApp.category.entity.Category;
import com.grab.FoodApp.category.repository.CategoryRepository;
import com.grab.FoodApp.cloudinary.CloudinaryService;
import com.grab.FoodApp.exceptions.BadRequestException;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.menu.dtos.MenuDTO;
import com.grab.FoodApp.menu.entity.Menu;
import com.grab.FoodApp.menu.repository.MenuRepository;
import com.grab.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    @SneakyThrows
    @Override
    public Response<MenuDTO> createMenu(MenuDTO menuDTO) {
        Category category = categoryRepository.findById(menuDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (menuDTO.getImageFile() == null || menuDTO.getImageFile().isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        Map<String, String> imageUrl = cloudinaryService.uploadFile(menuDTO.getImageFile());

        menuDTO.setImageUrl(imageUrl.get("url"));
        menuDTO.setImagePublicId(imageUrl.get("public_id"));

        var menu = modelMapper.map(menuDTO, Menu.class);
        menu.setCategory(category);
        var savedMenu = menuRepository.save(menu);
        var savedMenuDTO = modelMapper.map(savedMenu, MenuDTO.class);

        //Sort reviews by createdAt descending
        if (savedMenuDTO.getReviews() != null) {
            savedMenuDTO.getReviews().sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
        }

        return Response.<MenuDTO>builder()
                .data(savedMenuDTO)
                .message("Menu created successfully")
                .statusCode(HttpStatus.CREATED.value())
                .build();
    }

    @Override
    public Response<MenuDTO> getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found"));
        MenuDTO menuDTO = modelMapper.map(menu, MenuDTO.class);
        return Response.<MenuDTO>builder()
                .data(menuDTO)
                .message("Menu fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @SneakyThrows
    @Override
    public Response<MenuDTO> updateMenu(MenuDTO menuDTO) {
        Menu existingMenu = menuRepository.findById(menuDTO.getId())
                .orElseThrow(() -> new NotFoundException("Menu not found"));
        if (menuDTO.getName() != null && !menuDTO.getName().isEmpty()) {
            if (menuRepository.existsByName(menuDTO.getName()) &&
                    !existingMenu.getName().equals(menuDTO.getName())) {
                throw new BadRequestException("Menu name already exists");
            }
        }

        if (menuDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(menuDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            existingMenu.setCategory(category);
        }

        if (existingMenu.getImageUrl() != null && !existingMenu.getImageUrl().isEmpty() &&
                menuDTO.getImageFile() != null && !menuDTO.getImageFile().isEmpty()) {
            cloudinaryService.deleteFile(existingMenu.getImagePublicId());
            Map<String, String> imageUrl = cloudinaryService.uploadFile(menuDTO.getImageFile());
            existingMenu.setImageUrl(imageUrl.get("url"));
            existingMenu.setImagePublicId(imageUrl.get("public_id"));
        }

        existingMenu.setName(menuDTO.getName() != null ? menuDTO.getName() : existingMenu.getName());
        existingMenu.setDescription(menuDTO.getDescription() != null ? menuDTO.getDescription() : existingMenu.getDescription());
        existingMenu.setPrice(menuDTO.getPrice() != null ? menuDTO.getPrice() : existingMenu.getPrice());

        Menu updatedMenu = menuRepository.save(existingMenu);
        MenuDTO updatedMenuDTO = modelMapper.map(updatedMenu, MenuDTO.class);
        return Response.<MenuDTO>builder()
                .data(updatedMenuDTO)
                .message("Menu updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<?> deleteMenu(Long id) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found"));

        if (existingMenu.getImagePublicId() != null && !existingMenu.getImagePublicId().isEmpty()) {
            cloudinaryService.deleteFile(existingMenu.getImagePublicId());
            log.info("Deleted image with public ID: {}", existingMenu.getImagePublicId());
        }

        menuRepository.delete(existingMenu);
        return Response.builder()
                .message("Menu deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<List<MenuDTO>> getAllMenus(Long categoryId, String search) {
        Specification<Menu> spec = buildSpecification(categoryId, search);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        List<Menu> menus = menuRepository.findAll(spec, sort);

        List<MenuDTO> menuDTOs = menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .toList();

        return Response.<List<MenuDTO>>builder()
                .data(menuDTOs)
                .message("Menus fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    private Specification<Menu> buildSpecification(Long categoryId, String search) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (categoryId != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (search != null && !search.isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                        ));
            }

            return predicates;
        };
    }
}
