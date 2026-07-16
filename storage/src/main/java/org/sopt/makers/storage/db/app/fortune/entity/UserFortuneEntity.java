package org.sopt.makers.storage.db.app.fortune.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.fortune.UserFortune;

@Entity
@Table(
    name = "user_fortune",
    uniqueConstraints =
        @UniqueConstraint(name = "uq_user_fortune_user_id", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFortuneEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private Long fortuneWordId;

  private LocalDate checkedAt;

  public static UserFortuneEntity from(UserFortune userFortune) {
    return UserFortuneEntity.builder()
        .id(userFortune.id())
        .userId(userFortune.userId())
        .fortuneWordId(userFortune.fortuneWordId())
        .checkedAt(userFortune.checkedAt())
        .build();
  }

  public UserFortune toDomain() {
    return new UserFortune(id, userId, fortuneWordId, checkedAt);
  }
}
