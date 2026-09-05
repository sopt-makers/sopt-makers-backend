package org.sopt.makers.storage.db.playground.post.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.post.like.PostLike;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "community_post_like",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_community_post_like_post_user",
            columnNames = {"post_id", "user_id"}))
public class PostLikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "community_post_like_id")
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private PostLikeEntity(Long id, Long postId, Long userId) {
    this.id = id;
    this.postId = postId;
    this.userId = userId;
  }

  public PostLike toDomain() {
    return new PostLike(id, postId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static PostLikeEntity fromDomain(PostLike like) {
    return PostLikeEntity.builder()
        .id(like.id())
        .postId(like.postId())
        .userId(like.userId())
        .build();
  }
}
