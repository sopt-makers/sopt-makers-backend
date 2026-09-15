package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.home.HomeAppServices;

public record HomeAppServiceResponse(
    @Schema(description = "앱잼 기간인지 여부", example = "false") @JsonProperty("isAppjamMode")
        boolean isAppjamMode,
    @Schema(description = "홈에 띄울 앱 서비스 목록. 로그인 상태에서는 활동 여부에 따라 걸러진다")
        List<AppServiceEntryStatusResponse> appServices) {

  public static HomeAppServiceResponse of(HomeAppServices homeAppServices) {
    return new HomeAppServiceResponse(
        homeAppServices.isAppjamMode(),
        homeAppServices.appServices().stream().map(AppServiceEntryStatusResponse::of).toList());
  }
}
