package org.sopt.makers.storage.db.playground.community.anonymous.entity;

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
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "anonymous_profile_image")
public class AnonymousProfileImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "anonymous_profile_image_id")
  private Long id;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Builder(access = PROTECTED)
  private AnonymousProfileImageEntity(Long id, String imageUrl) {
    this.id = id;
    this.imageUrl = imageUrl;
  }

  public AnonymousProfileImage toDomain() {
    return new AnonymousProfileImage(id, imageUrl);
  }
}
