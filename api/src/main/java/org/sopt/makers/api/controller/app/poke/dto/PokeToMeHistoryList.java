package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.poke.PokeToMeHistoryData;

public record PokeToMeHistoryList(
    @Schema(description = "나를 찌른 친구 목록") List<SimplePokeProfile> history,
    @Schema(description = "전체 페이지 수", example = "3") int totalPageSize,
    @Schema(description = "한 페이지 크기", example = "10") int pageSize,
    @Schema(description = "0부터 시작하는 현재 페이지 번호", example = "0") int pageNum) {

  public static PokeToMeHistoryList of(PokeToMeHistoryData data) {
    return new PokeToMeHistoryList(
        data.history().stream().map(SimplePokeProfile::of).toList(),
        data.totalPageSize(),
        data.pageSize(),
        data.pageNum());
  }
}
