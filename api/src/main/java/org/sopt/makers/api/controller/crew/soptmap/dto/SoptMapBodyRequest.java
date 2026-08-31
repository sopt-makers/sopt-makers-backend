package org.sopt.makers.api.controller.crew.soptmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record SoptMapBodyRequest(
    @NotBlank String placeName,
    @NotNull @Size(max = 3) List<String> stationNames,
    @NotBlank @Size(max = 500) String description,
    @NotNull @Size(max = 2) List<MapTag> tags,
    String naverLink,
    String kakaoLink) {

  public SoptMapService.CreateSoptMapCommand toCreateCommand() {
    return new SoptMapService.CreateSoptMapCommand(toValues(), stationNames);
  }

  public SoptMapService.UpdateSoptMapCommand toUpdateCommand() {
    return new SoptMapService.UpdateSoptMapCommand(toValues(), stationNames);
  }

  private SoptMap.Values toValues() {
    return new SoptMap.Values(placeName, description, tags, naverLink, kakaoLink);
  }
}
