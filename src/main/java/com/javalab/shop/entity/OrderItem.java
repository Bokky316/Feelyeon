package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // OrderItem과 Order는 다대일 관계
    // OrderItem이 Order에 속해있다.
    // @JoinColumn은 외래 키를 관리하는 필드에 사용되며, 연관관계의 주인을 나타냅니다.
    @ManyToOne(fetch = FetchType.LAZY)  // 기본은 EAGER, LAZY로 설정하면 지연로딩
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;   //주문가격

    private int count;    //수량

    //private LocalDateTime regTime; //등록 시간
    //private LocalDateTime updateTime; //수정 시간

    /**
     * 주문 상품 생성 메서드
     * - Item, 수량을 받아서 OrderItem 생성
     */
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();  // 주문 상품 생성
        orderItem.setItem(item);    // 주문 상품 설정
        orderItem.setCount(count);  // 주문 수량
        orderItem.setOrderPrice(item.getPrice());   // 주문 가격은 상품의 가격
        item.removeStock(count);    // 주문 수량만큼 재고 감소
        return orderItem;
    }

    /**
     * 총 주문 가격 계산
     * @return
     */
    public int getTotalPrice(){
        return orderPrice * count;
    }

    /**
     * 주문 취소
     * - 주문 수량만큼 재고 증가
     */
    public void cancel(){
        this.getItem().addStock(count);
    }
}
