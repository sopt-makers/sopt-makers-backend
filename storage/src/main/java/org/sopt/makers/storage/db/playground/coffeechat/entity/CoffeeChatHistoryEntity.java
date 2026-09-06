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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_chat_history")
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
