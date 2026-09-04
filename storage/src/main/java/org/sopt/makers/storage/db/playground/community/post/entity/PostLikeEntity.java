package org.sopt.makers.storage.db.playground.community.post.entity;

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
import org.sopt.makers.domain.playground.community.post.PostLike;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "community_post_like")
public class PostLikeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "community_post_like_id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Builder(access = PROTECTED)
  private PostLikeEntity(Long userId, Long postId) {
    this.userId = userId;
    this.postId = postId;
  }

  public static PostLikeEntity from(PostLike postLike) {
    return PostLikeEntity.builder().userId(postLike.userId()).postId(postLike.postId()).build();
  }

  public PostLike toDomain() {
    return new PostLike(id, userId, postId, getCreatedAt(), getUpdatedAt());
  }
}
