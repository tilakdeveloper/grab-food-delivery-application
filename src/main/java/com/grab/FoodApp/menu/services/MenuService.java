package com.grab.FoodApp.menu.services;

import com.grab.FoodApp.menu.dtos.MenuDTO;
import com.grab.FoodApp.response.Response;

import java.util.List;

public interface MenuService {

    Response<MenuDTO> createMenu(MenuDTO menuDTO);

    Response<MenuDTO> getMenuById(Long id);

    Response<MenuDTO> updateMenu(MenuDTO menuDTO);

    Response<?> deleteMenu(Long id);

    Response<List<MenuDTO>> getAllMenus(Long categoryId, String search);
}
