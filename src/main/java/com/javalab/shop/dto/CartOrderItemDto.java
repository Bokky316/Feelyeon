package com.javalab.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOrderItemDto {
    private Long cartItemId;  // 장바구니 아이템 ID
    private int count;        // 수량 (필요 시 추가)
}
