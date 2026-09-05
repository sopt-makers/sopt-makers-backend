package org.sopt.makers.api.controller.app.poke.dto;

import java.util.List;
import org.sopt.makers.domain.app.poke.PokeToMeHistoryData;

public record PokeToMeHistoryList(
    List<SimplePokeProfile> history, int totalPageSize, int pageSize, int pageNum) {

  public static PokeToMeHistoryList of(PokeToMeHistoryData data) {
    return new PokeToMeHistoryList(
        data.history().stream().map(SimplePokeProfile::of).toList(),
        data.totalPageSize(),
        data.pageSize(),
        data.pageNum());
  }
}
