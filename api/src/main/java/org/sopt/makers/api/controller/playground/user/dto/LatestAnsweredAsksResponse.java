package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.ask.LatestAnsweredAskCard;

public record LatestAnsweredAsksResponse(List<LatestAskCardResponse> questions) {

  public static LatestAnsweredAsksResponse from(List<LatestAnsweredAskCard> cards) {
    return new LatestAnsweredAsksResponse(cards.stream().map(LatestAskCardResponse::from).toList());
  }

  public record LatestAskCardResponse(
      Long receiverId,
      String receiverName,
      String receiverProfileImage,
      Long questionId,
      String content,
      AskLocationResponse location) {

    public static LatestAskCardResponse from(LatestAnsweredAskCard card) {
      return new LatestAskCardResponse(
          card.receiverUserId(),
          card.receiverName(),
          card.receiverProfileImage(),
          card.questionId(),
          card.content(),
          AskLocationResponse.from(card.location()));
    }
  }
}
