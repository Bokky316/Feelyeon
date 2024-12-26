package com.javalab.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderRequestDto {
    private List<CartOrderItemDto> cartOrderItems;  // 장바구니 주문 항목 리스트
}
