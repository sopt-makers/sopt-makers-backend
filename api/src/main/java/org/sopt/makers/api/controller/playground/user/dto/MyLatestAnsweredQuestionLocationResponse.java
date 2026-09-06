package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.ask.MyLatestAnsweredAskLocation;

public record MyLatestAnsweredQuestionLocationResponse(Long questionId, Integer page, Integer index) {

  public static MyLatestAnsweredQuestionLocationResponse from(MyLatestAnsweredAskLocation location) {
    return new MyLatestAnsweredQuestionLocationResponse(
        location.questionId(), location.page(), location.index());
  }
}
