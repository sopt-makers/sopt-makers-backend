package org.sopt.makers.storage.db.app.poke.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "friend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private Long friendUserId;

  private int pokeCount;

  @Column(length = 30)
  private String anonymousName;

  public static FriendEntity from(Friend friend) {
    return FriendEntity.builder()
        .id(friend.id())
        .userId(friend.userId())
        .friendUserId(friend.friendUserId())
        .pokeCount(friend.pokeCount())
        .anonymousName(friend.anonymousName())
        .build();
  }

  public Friend toDomain() {
    return new Friend(id, userId, friendUserId, pokeCount, anonymousName);
  }
}
