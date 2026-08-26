package org.sopt.makers.storage.db.app.push.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.storage.db.app.push.converter.PushTokenPlatformConverter;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "push_token",
    indexes = @Index(name = "idx_push_token_user_id", columnList = "user_id"))
public class PushTokenEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "token", nullable = false, columnDefinition = "TEXT")
  private String token;

  @Convert(converter = PushTokenPlatformConverter.class)
  @Column(name = "platform", nullable = false, length = 20)
  private PushTokenPlatform platform;

  private PushTokenEntity(PushToken pushToken) {
    this.userId = pushToken.userId();
    this.token = pushToken.token();
    this.platform = pushToken.platform();
  }

  public static PushTokenEntity from(PushToken pushToken) {
    return new PushTokenEntity(pushToken);
  }

  public PushToken toDomain() {
    return new PushToken(id, userId, token, platform);
  }
}
