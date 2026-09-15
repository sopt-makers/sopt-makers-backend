package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.ask.MyLatestAnsweredAskLocation;

public record MyLatestAnsweredAskLocationResponse(Long questionId, Integer page, Integer index) {

  public static MyLatestAnsweredAskLocationResponse from(MyLatestAnsweredAskLocation location) {
    return new MyLatestAnsweredAskLocationResponse(
        location.questionId(), location.page(), location.index());
  }
}
