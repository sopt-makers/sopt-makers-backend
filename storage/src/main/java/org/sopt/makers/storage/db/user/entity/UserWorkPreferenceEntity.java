package org.sopt.makers.storage.db.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.enums.CommunicationStyle;
import org.sopt.makers.domain.user.enums.FeedbackStyle;
import org.sopt.makers.domain.user.enums.IdeationStyle;
import org.sopt.makers.domain.user.enums.WorkPlace;
import org.sopt.makers.domain.user.enums.WorkTime;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_work_preferences")
public class UserWorkPreferenceEntity extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  private IdeationStyle ideationStyle;

  @Enumerated(EnumType.STRING)
  private WorkTime workTime;

  @Enumerated(EnumType.STRING)
  private CommunicationStyle communicationStyle;

  @Enumerated(EnumType.STRING)
  private WorkPlace workPlace;

  @Enumerated(EnumType.STRING)
  private FeedbackStyle feedbackStyle;

  @Builder(access = AccessLevel.PRIVATE)
  private UserWorkPreferenceEntity(
      final UserEntity user,
      final IdeationStyle ideationStyle,
      final WorkTime workTime,
      final CommunicationStyle communicationStyle,
      final WorkPlace workPlace,
      final FeedbackStyle feedbackStyle) {
    this.user = user;
    this.ideationStyle = ideationStyle;
    this.workTime = workTime;
    this.communicationStyle = communicationStyle;
    this.workPlace = workPlace;
    this.feedbackStyle = feedbackStyle;
  }

  public static UserWorkPreferenceEntity from(
      final UserEntity user, final WorkPreference workPreference) {
    return UserWorkPreferenceEntity.builder()
        .user(user)
        .ideationStyle(workPreference.ideationStyle())
        .workTime(workPreference.workTime())
        .communicationStyle(workPreference.communicationStyle())
        .workPlace(workPreference.workPlace())
        .feedbackStyle(workPreference.feedbackStyle())
        .build();
  }

  public void update(final WorkPreference workPreference) {
    this.ideationStyle = workPreference.ideationStyle();
    this.workTime = workPreference.workTime();
    this.communicationStyle = workPreference.communicationStyle();
    this.workPlace = workPreference.workPlace();
    this.feedbackStyle = workPreference.feedbackStyle();
  }

  public WorkPreference toDomain() {
    return WorkPreference.of(ideationStyle, workTime, communicationStyle, workPlace, feedbackStyle);
  }
}
