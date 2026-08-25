package org.sopt.makers.api.controller.app.fortune.dto;

import org.sopt.makers.domain.app.fortune.FortuneCard;

public record FortuneCardResponse(
    String name, String description, String imageUrl, String imageColorCode) {

  public static FortuneCardResponse of(FortuneCard fortuneCard) {
    return new FortuneCardResponse(
        fortuneCard.name(),
        fortuneCard.description(),
        fortuneCard.imageUrl(),
        fortuneCard.imageColorCode());
  }
}
