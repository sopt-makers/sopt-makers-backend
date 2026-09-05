package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.SoptampUser;

public record SoptampUserResponse(String nickname, Long points, String profileMessage) {

  public static SoptampUserResponse of(SoptampUser soptampUser) {
    return new SoptampUserResponse(
        soptampUser.nickname(), soptampUser.totalPoints(), soptampUser.profileMessage());
  }
}
