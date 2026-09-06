package org.sopt.makers.storage.db.playground.member.profile.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_activity_check")
public class UserActivityCheckEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  @Column(name = "edit_activities_able", nullable = false)
  private Boolean editActivitiesAble;

  private UserActivityCheckEntity(Long userId, Boolean editActivitiesAble) {
    this.userId = userId;
    this.editActivitiesAble = editActivitiesAble;
  }

  public static UserActivityCheckEntity create(Long userId, boolean editActivitiesAble) {
    return new UserActivityCheckEntity(userId, editActivitiesAble);
  }

  public void updateEditActivitiesAble(boolean editActivitiesAble) {
    this.editActivitiesAble = editActivitiesAble;
  }
}
