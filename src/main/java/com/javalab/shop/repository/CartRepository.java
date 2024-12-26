package com.javalab.shop.repository;

import com.javalab.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 1. 현재 로그인한 회원의 장바구니 조회
    Cart findByMemberId(Long memberId);
}
