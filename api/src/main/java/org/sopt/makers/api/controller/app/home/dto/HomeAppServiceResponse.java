package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.sopt.makers.domain.app.home.HomeAppServices;

public record HomeAppServiceResponse(
    @JsonProperty("isAppjamMode") boolean isAppjamMode,
    List<AppServiceEntryStatusResponse> appServices) {

  public static HomeAppServiceResponse of(HomeAppServices homeAppServices) {
    return new HomeAppServiceResponse(
        homeAppServices.isAppjamMode(),
        homeAppServices.appServices().stream().map(AppServiceEntryStatusResponse::of).toList());
  }
}
