package org.sopt.makers.api.controller.playground.member.dto;

public record AnonymousProfileResponse(String nickname, String profileImgUrl) {

  public static AnonymousProfileResponse from(String nickname, String profileImgUrl) {
    if (nickname == null) {
      return null;
    }
    return new AnonymousProfileResponse(nickname, profileImgUrl);
  }
}
