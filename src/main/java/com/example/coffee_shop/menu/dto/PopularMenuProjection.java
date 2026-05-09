package com.example.coffee_shop.menu.dto;

public interface PopularMenuProjection {

    Long getMenuId();

    String getMenuName();

    Long getOrderCount();
}