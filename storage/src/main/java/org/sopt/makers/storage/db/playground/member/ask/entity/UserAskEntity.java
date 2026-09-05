package org.sopt.makers.storage.db.playground.member.ask.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.UserAsk;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member_question")
public class UserAskEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id")
  private Long id;

  @Column(name = "receiver_id", nullable = false)
  private Long receiverUserId;

  @Column(name = "asker_id")
  private Long askerUserId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "is_anonymous", nullable = false)
  private Boolean isAnonymous;

  @Column(name = "anonymous_nickname_id")
  private Long anonymousNicknameId;

  @Column(name = "anonymous_profile_image_id")
  private Long anonymousProfileImageId;

  @Column(name = "is_reported", nullable = false)
  private Boolean isReported;

  @Builder(access = PRIVATE)
  private UserAskEntity(
      Long id,
      Long receiverUserId,
      Long askerUserId,
      String content,
      Boolean isAnonymous,
      Long anonymousNicknameId,
      Long anonymousProfileImageId,
      Boolean isReported) {
    this.id = id;
    this.receiverUserId = receiverUserId;
    this.askerUserId = askerUserId;
    this.content = content;
    this.isAnonymous = isAnonymous;
    this.anonymousNicknameId = anonymousNicknameId;
    this.anonymousProfileImageId = anonymousProfileImageId;
    this.isReported = isReported;
  }

  public UserAsk toDomain() {
    return new UserAsk(
        id,
        receiverUserId,
        askerUserId,
        content,
        isAnonymous,
        anonymousNicknameId,
        anonymousProfileImageId,
        isReported,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static UserAskEntity fromDomain(UserAsk userAsk) {
    return UserAskEntity.builder()
        .id(userAsk.id())
        .receiverUserId(userAsk.receiverUserId())
        .askerUserId(userAsk.askerUserId())
        .content(userAsk.content())
        .isAnonymous(userAsk.isAnonymous())
        .anonymousNicknameId(userAsk.anonymousNicknameId())
        .anonymousProfileImageId(userAsk.anonymousProfileImageId())
        .isReported(userAsk.isReported())
        .build();
  }
}
