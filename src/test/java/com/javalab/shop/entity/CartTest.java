package com.javalab.shop.entity;

import com.javalab.shop.dto.MemberFormDto;
import com.javalab.shop.repository.CartRepository;
import com.javalab.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Log4j2
public class CartTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    // 회원생성 메서드
    public Member createMember() {
        MemberFormDto dto = MemberFormDto.builder()
                .email("test1@test.com")
                .name("홍길동")
                .address("서울시 마포구 합정동")
                .password("1234")
                .build();
        Member member = Member.createMember(dto, passwordEncoder);
        return member;
    }

    // 장바구니 회원 엔티티 매핑 조회 테스트
    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartMemberTest() {
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영, 이후 쿼리에서 최신 데이터를 읽어올 수 있음.
        em.flush();
        // 영속성 컨텍스트 초기화, 영속성 컨텍스트의 캐시를 비움, 관리중인 엔티티를 초기화(deteched)함.
        // 영속성 컨텍스트를 초기화하면 1차 캐시에 저장된 엔티티를 사용하지 않고 데이터베이스에서 새로 조회함.
        // 즉, 데이터베이스에서 최신 데이터를 읽어옴.
        em.clear();

        Cart saveCart = cartRepository.findById(cart.getId()).orElseThrow();
        log.info("cart : " + saveCart);
        log.info("cart.member : " + saveCart.getMember());
        assertEquals(saveCart.getMember().getId(), member.getId());
    }
}
