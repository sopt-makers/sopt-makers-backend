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
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "anonymous_nickname")
public class AnonymousNicknameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "anonymous_nickname_id")
  private Long id;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Builder(access = PROTECTED)
  private AnonymousNicknameEntity(Long id, String nickname) {
    this.id = id;
    this.nickname = nickname;
  }

  public AnonymousNickname toDomain() {
    return new AnonymousNickname(id, nickname);
  }
}
