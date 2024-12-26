package com.javalab.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 장바구니 주문 DTO
 * - 장바구니에 담긴 상품을 주문할 때 사용하는 DTO
 * - 장바구니에 담긴 상품의 ID와 수량을 담는다.
 *
 */
@Getter
@Setter
public class CartOrderDto {

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;

}