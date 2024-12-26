package com.javalab.shop.entity;

import com.javalab.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity{

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;   //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;  //주문상태

    /*
        * @OneToMany: 일대다 관계 설정
        * mappedBy: 연관관계의 주인을 설정, 연관관계의 주인이 자신이 아니라 OrderItem 엔티티의 order 필드임을 명시
        * JPA는 이 필드가 단순히 읽기 전용임을 알고, 외래 키 관리에 사용하지 않습니다.
        * CascadeType.ALL : Order 엔티티를 저장하거나 삭제할 때, 연관된 OrderItem 엔티티도 함께 저장하거나 삭제
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    //private LocalDateTime updateTime; //수정 시간
    //private LocalDateTime regTime; //등록 시간


    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);

        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    /**
     * 주문 취소
     * 주문 취소 시, 주문 상태를 취소로 변경하고, 주문 상품 리스트의 상품 수량을 순회 하면서 취소 처리
     */
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}
