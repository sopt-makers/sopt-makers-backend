package org.sopt.makers.storage.db.playground.community.anonymous.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "anonymous_profile",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_anonymous_profile_user_post",
          columnNames = {"user_id", "post_id"})
    })
public class AnonymousProfileEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "anonymous_profile_id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "anonymous_nickname_id", nullable = false)
  private AnonymousNicknameEntity nickname;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "anonymous_profile_image_id", nullable = false)
  private AnonymousProfileImageEntity profileImage;

  @Builder(access = PROTECTED)
  private AnonymousProfileEntity(
      Long userId, Long postId, AnonymousNicknameEntity nickname, AnonymousProfileImageEntity profileImage) {
    this.userId = userId;
    this.postId = postId;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }

  public static AnonymousProfileEntity of(
      Long userId, Long postId, AnonymousNicknameEntity nickname, AnonymousProfileImageEntity profileImage) {
    return AnonymousProfileEntity.builder()
        .userId(userId)
        .postId(postId)
        .nickname(nickname)
        .profileImage(profileImage)
        .build();
  }

  public AnonymousProfile toDomain() {
    return new AnonymousProfile(
        id, userId, postId, nickname.toDomain(), profileImage.toDomain(), getCreatedAt(), getUpdatedAt());
  }
}
