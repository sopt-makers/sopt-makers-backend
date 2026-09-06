package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.ask.AskLocation;

public record QuestionLocationResponse(Long questionId, String tab, Integer page, Integer index) {

  public static QuestionLocationResponse from(AskLocation location) {
    return new QuestionLocationResponse(
        location.questionId(), location.tab().getValue(), location.page(), location.index());
  }
}
