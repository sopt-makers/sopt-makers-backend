package org.sopt.makers.api.controller.app.fortune.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.fortune.FortuneCard;

public record FortuneCardResponse(
    @Schema(description = "운세카드 이름", example = "행운의 시작") String name,
    @Schema(description = "운세카드 설명", example = "작은 시도가 큰 결과로 이어지는 하루예요") String description,
    @Schema(description = "운세카드 이미지 주소", example = "https://s3.sopt.org/fortune/card-01.png")
        String imageUrl,
    @Schema(description = "운세카드 대표 색상 코드", example = "#FFB300") String imageColorCode) {

  public static FortuneCardResponse of(FortuneCard fortuneCard) {
    return new FortuneCardResponse(
        fortuneCard.name(),
        fortuneCard.description(),
        fortuneCard.imageUrl(),
        fortuneCard.imageColorCode());
  }
}
