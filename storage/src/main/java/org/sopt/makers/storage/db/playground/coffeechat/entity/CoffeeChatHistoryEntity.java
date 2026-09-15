package org.sopt.makers.storage.db.playground.coffeechat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.storage.db.common.BaseEntity;


/**
 * CoffeeChat 도메인 마이그레이션 이전까지 사용하는 최소 매핑. {@code coffee_chat_history} 테이블의 송/수신자 집계에만 필요한 컬럼만 조회한다.
 */
@Entity
@Table(name = "coffee_chat_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeChatHistoryEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "receiver_id")
  private Long receiverId;

  @Column(name = "sender_id")
  private Long senderId;

  @Column(name = "request_content", columnDefinition = "TEXT")
  private String requestContent;

  private CoffeeChatHistoryEntity(Long receiverId, Long senderId, String requestContent) {
    this.receiverId = receiverId;
    this.senderId = senderId;
    this.requestContent = requestContent;
  }

  public static CoffeeChatHistoryEntity of(Long receiverId, Long senderId, String requestContent) {
    return new CoffeeChatHistoryEntity(receiverId, senderId, requestContent);
  }

}
