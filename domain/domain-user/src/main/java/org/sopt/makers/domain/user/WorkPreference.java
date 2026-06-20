package org.sopt.makers.domain.user;

import org.sopt.makers.domain.user.enums.CommunicationStyle;
import org.sopt.makers.domain.user.enums.FeedbackStyle;
import org.sopt.makers.domain.user.enums.IdeationStyle;
import org.sopt.makers.domain.user.enums.WorkPlace;
import org.sopt.makers.domain.user.enums.WorkTime;

public record WorkPreference(
    IdeationStyle ideationStyle,
    WorkTime workTime,
    CommunicationStyle communicationStyle,
    WorkPlace workPlace,
    FeedbackStyle feedbackStyle) {

  public static WorkPreference of(
      IdeationStyle ideationStyle,
      WorkTime workTime,
      CommunicationStyle communicationStyle,
      WorkPlace workPlace,
      FeedbackStyle feedbackStyle) {
    return new WorkPreference(
        ideationStyle, workTime, communicationStyle, workPlace, feedbackStyle);
  }
}
