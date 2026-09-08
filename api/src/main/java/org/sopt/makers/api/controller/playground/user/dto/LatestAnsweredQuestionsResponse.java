package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.ask.LatestAnsweredAskCard;

public record LatestAnsweredQuestionsResponse(List<LatestQuestionCardResponse> questions) {

  public static LatestAnsweredQuestionsResponse from(List<LatestAnsweredAskCard> cards) {
    return new LatestAnsweredQuestionsResponse(
        cards.stream().map(LatestQuestionCardResponse::from).toList());
  }

  public record LatestQuestionCardResponse(
      Long receiverId,
      String receiverName,
      String receiverProfileImage,
      Long questionId,
      String content,
      QuestionLocationResponse location) {

    public static LatestQuestionCardResponse from(LatestAnsweredAskCard card) {
      return new LatestQuestionCardResponse(
          card.receiverUserId(),
          card.receiverName(),
          card.receiverProfileImage(),
          card.questionId(),
          card.content(),
          QuestionLocationResponse.from(card.location()));
    }
  }
}
