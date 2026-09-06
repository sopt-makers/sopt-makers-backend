package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import org.sopt.makers.domain.playground.wordchaingame.port.WordChainGameUserPort.UserInfo;

public record UserResponse(Long id, String name, String profileImage) {

  public static UserResponse from(UserInfo info) {
    if (info == null) return null;
    return new UserResponse(info.id(), info.name(), info.profileImage());
  }
}
