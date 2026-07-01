package org.sopt.makers.storage.db.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.storage.db.common.BaseEntity;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_links")
public class UserLinkEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
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

  public static UserLinkEntity fromLinkForUser(final Long userId, final UserLink userLink) {
    return UserLinkEntity.builder()
        .userId(userId)
        .title(userLink.title())
        .url(userLink.url())
        .build();
  }

  public UserLink toDomain() {
    return UserLink.of(getId(), userId, title, url);
  }
}
