package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;

public record PartRankResponse(
    @Schema(description = "파트 이름", example = "서버") String part,
    @Schema(description = "파트 순위. 1부터 시작", example = "1") Integer rank,
    @Schema(description = "파트 점수에 100을 곱한 값을 정수로 담은 값. 앱 하위 호환용", example = "12050") Long points,
    @Schema(description = "파트 점수(총점 ÷ √인원, 소수 둘째 자리까지 반올림)에 100을 곱한 값", example = "12050.00")
        BigDecimal pointsDecimal) {

  public static PartRankResponse of(PartRank partRank) {
    return new PartRankResponse(
        partRank.part(), partRank.rank(), partRank.points(), partRank.pointsDecimal());
  }
}
