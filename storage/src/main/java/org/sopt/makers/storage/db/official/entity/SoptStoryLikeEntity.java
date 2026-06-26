package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
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
import org.sopt.makers.domain.official.soptstory.SoptStoryLike;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "\"SoptStoryLike\"",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_soptstory_like_ip",
          columnNames = {"\"soptStoryId\"", "\"ip\""})
    })
public class SoptStoryLikeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"soptStoryId\"", nullable = false)
  private SoptStoryEntity soptStory;

  @Column(name = "\"ip\"", nullable = false, length = 45)
  private String ip;

  @Builder(access = PRIVATE)
  private SoptStoryLikeEntity(Long id, SoptStoryEntity soptStory, String ip) {
    this.id = id;
    this.soptStory = soptStory;
    this.ip = ip;
  }

  public SoptStoryLike toDomain() {
    return new SoptStoryLike(id, soptStory.getId(), ip);
  }

  public static SoptStoryLikeEntity fromDomain(SoptStoryLike like) {
    return SoptStoryLikeEntity.builder()
        .id(like.id())
        .soptStory(SoptStoryEntity.reference(like.soptStoryId()))
        .ip(like.ip())
        .build();
  }
}
