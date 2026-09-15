package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public record ProfileMessageResponse(
    @Schema(description = "바뀐 프로필 한마디", example = "오늘도 화이팅") String profileMessage) {

  public static ProfileMessageResponse of(SoptampUser soptampUser) {
    return new ProfileMessageResponse(soptampUser.profileMessage());
  }
}
