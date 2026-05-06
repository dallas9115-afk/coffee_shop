package com.example.coffee_shop.order.dto;

public interface PopularMenuProjection {

    Long getMenuId();

    String getMenuName();

    Long getOrderCount();
}
