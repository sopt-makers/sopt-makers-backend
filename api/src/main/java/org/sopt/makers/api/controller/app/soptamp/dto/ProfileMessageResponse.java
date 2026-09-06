package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.SoptampUser;

public record ProfileMessageResponse(String profileMessage) {

  public static ProfileMessageResponse of(SoptampUser soptampUser) {
    return new ProfileMessageResponse(soptampUser.profileMessage());
  }
}
