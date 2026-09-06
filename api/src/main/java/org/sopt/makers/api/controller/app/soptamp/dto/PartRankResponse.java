package org.sopt.makers.api.controller.app.soptamp.dto;

import java.math.BigDecimal;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;

public record PartRankResponse(String part, Integer rank, Long points, BigDecimal pointsDecimal) {

  public static PartRankResponse of(PartRank partRank) {
    return new PartRankResponse(
        partRank.part(), partRank.rank(), partRank.points(), partRank.pointsDecimal());
  }
}
