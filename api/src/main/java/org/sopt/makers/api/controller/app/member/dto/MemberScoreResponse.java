package org.sopt.makers.api.controller.app.member.dto;

public record MemberScoreResponse(float score) {

  public static MemberScoreResponse from(float score) {
    return new MemberScoreResponse(score);
  }
}
