package com.example.coffee_shop.common.exception;

public class MenuNotFoundException extends BusinessException {

    public MenuNotFoundException(Long menuId) {
        super(404, "MENU_NOT_FOUND", "존재하지 않는 메뉴입니다. (menuId: " + menuId + ")");
    }
}
