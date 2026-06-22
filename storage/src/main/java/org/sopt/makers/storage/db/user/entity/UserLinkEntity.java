package org.sopt.makers.storage.db.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.user.UserLink;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_links")
public class UserLinkEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private String title;

  private String url;

  @Builder(access = AccessLevel.PRIVATE)
  private UserLinkEntity(final Long userId, final String title, final String url) {
    this.userId = userId;
    this.title = title;
    this.url = url;
  }

  public static UserLinkEntity from(final UserLink userLink) {
    return UserLinkEntity.builder()
        .userId(userLink.userId())
        .title(userLink.title())
        .url(userLink.url())
        .build();
  }

  public UserLink toDomain() {
    return UserLink.of(id, userId, title, url);
  }
}
