package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.ask.AskLocation;

public record AskLocationResponse(Long questionId, String tab, Integer page, Integer index) {

  public static AskLocationResponse from(AskLocation location) {
    return new AskLocationResponse(
        location.questionId(), location.tab().getValue(), location.page(), location.index());
  }
}
