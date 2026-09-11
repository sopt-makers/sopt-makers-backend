package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;

public record PartRankResponse(
    @Schema(description = "파트 이름", example = "서버") String part,
    @Schema(description = "파트 순위. 1부터 시작", example = "1") Integer rank,
    @Schema(description = "파트 점수에서 소수부를 버린 값. 앱 하위 호환용", example = "120") Long points,
    @Schema(description = "파트 점수 원본", example = "120.50") BigDecimal pointsDecimal) {

  public static PartRankResponse of(PartRank partRank) {
    return new PartRankResponse(
        partRank.part(), partRank.rank(), partRank.points(), partRank.pointsDecimal());
  }
}
