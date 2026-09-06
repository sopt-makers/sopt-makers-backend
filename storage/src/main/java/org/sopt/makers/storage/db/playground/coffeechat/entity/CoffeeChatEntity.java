package org.sopt.makers.storage.db.playground.coffeechat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CoffeeChat 도메인 마이그레이션 이전까지 사용하는 최소 매핑. {@code coffee_chat} 테이블의 활성화 여부만 조회한다.
 * 나머지 컬럼(career, section 등)은 CoffeeChat 도메인 이관 시 별도 엔티티에서 다룬다.
 */
@Entity
@Table(name = "coffee_chat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeChatEntity {

  @Id private Long id;

  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "is_coffee_chat_activate")
  private Boolean isCoffeeChatActivate;
}
