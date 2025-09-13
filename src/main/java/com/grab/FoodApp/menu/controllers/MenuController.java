package com.grab.FoodApp.menu.controllers;

import com.grab.FoodApp.menu.dtos.MenuDTO;
import com.grab.FoodApp.menu.services.MenuService;
import com.grab.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDTO>> createMenu(@ModelAttribute @Valid MenuDTO menuDTO,
                                                        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        menuDTO.setImageFile(imageFile);
        Response<MenuDTO> response = menuService.createMenu(menuDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDTO>> updateMenu(@ModelAttribute @Valid MenuDTO menuDTO,
                                                        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        menuDTO.setImageFile(imageFile);
        Response<MenuDTO> response = menuService.updateMenu(menuDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MenuDTO>> getMenuById(@PathVariable Long id) {
        Response<MenuDTO> response = menuService.getMenuById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> deleteMenu(@PathVariable Long id) {
        Response<?> response = menuService.deleteMenu(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<List<MenuDTO>>> getAllMenus(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "search", required = false) String search) {
        Response<java.util.List<MenuDTO>> response = menuService.getAllMenus(categoryId, search);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
