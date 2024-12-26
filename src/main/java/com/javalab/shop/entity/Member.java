package com.javalab.shop.entity;

import com.javalab.shop.constant.Role;
import com.javalab.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원 엔티티
 * - 회원 정보를 저장하는 엔티티 클래스
 * - 회원 정보를 저장하는 테이블과 매핑된다.
 * - 주로 서비스 레이어와 리포지토리 레이어에서 사용된다.
 * - 화면에서 데이터를 전달받는 용도로는 사용하지 않는게 관례이다.
 */
@Entity
@Table(name = "member")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    // 이메일은 중복될 수 없다. unique = true
    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    // 회원의 권한을 나타내는 열거형 상수, 한 사용자가 다수의 권한을 가질 수 있다.
    @Enumerated(EnumType.STRING)
    private Role role;

    /*
        * 회원 엔티티 생성 정적 메서드
        * - MemberFormDto의 값들이 -> Member 엔티티로 이동
        * - 회원가입 폼 DTO를 전달받아 회원 엔티티를 생성하는 역할을 한다.
        * - Member 객체 생성 로직을 엔티티 내부에 숨기고, 외부에서는 이 메서드를 통해 객체를 생성하도록 한다.
        * - 이 메소드를 만들어 두면 외부에서 이 엔티티 객체를 생성하고 값을 할당하는 코드를 중복으로 작성할 필요가 없다.
        * - 정적 메소드이기 때문에 외부에 객체 생성없이 바로 호출이 가능하다는 장점이 있다.
        * - Member 엔티티의 속성이 변화된다고 할지라도 여기서만 바꿔주면 된다.
        * - passwordEncoder.encode : 비밀번호 암호화 함수
        * - 사용자가 입력한 암호는 "평문"이다. 즉 암호화가 안된 문자열이다.
     */
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        String password = passwordEncoder.encode(memberFormDto.getPassword()); // 비밀번호 암호화
        member.setPassword(password);
        member.setAddress(memberFormDto.getAddress());
        member.setRole(Role.USER);  // 회원가입 시 사용자의 권한 : USER
        return member;
    }
}
