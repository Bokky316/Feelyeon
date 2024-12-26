package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 생성, 마리아디비, MySQL은 AUTO increment
    @Column(name="cart_item_id")
    private Long id;

    @ManyToOne  // 다대일 관계, 카트 아이템 입장에서 바라보는 카트는 하나, 기본전략이 EAGER
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 카트 아이템 입장에서 바라보는 상품은 하나, 기본전략이 EAGER
    @JoinColumn(name="item_id")
    private Item item;

    /**
     * 장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜주는 메소드
     *
     * @param cart
     * @param item
     * @param count
     * @return
     */
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    /**
     * 기존에 장바구니에 담겨있는 상품인데 해당 상품을 추가로 장바구니에 담을 때
     * 기존 상품의 수량을 증가시켜주는 메소드
     */
    public void addCount(int count){
        this.count += count;
    }

    private int count;

    /**
     * 장바구니에 담긴 상품의 수량을 수정하는 메소드
     */
    public void updateCount(int count){
        this.count = count;
    }
}
