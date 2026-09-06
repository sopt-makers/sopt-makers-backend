package org.sopt.makers.storage.db.playground.coffeechat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CoffeeChat 도메인 마이그레이션 이전까지 사용하는 최소 매핑. {@code coffee_chat_history} 테이블의 송/수신자 집계에만 필요한
 * 컬럼만 조회한다.
 */
@Entity
@Table(name = "coffee_chat_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeChatHistoryEntity {

  @Id private Long id;

  @Column(name = "receiver_id")
  private Long receiverId;

  @Column(name = "sender_id")
  private Long senderId;
}
