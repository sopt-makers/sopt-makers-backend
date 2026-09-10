package org.sopt.makers.api.controller.playground.user.dto;

import org.sopt.makers.domain.playground.member.profile.UserSummary;

public record UserBlockResponse(
    Boolean status, BlockMemberInfo blockingMember, BlockMemberInfo blockedMember) {

  public record BlockMemberInfo(Long id, String name) {}

  public static UserBlockResponse of(
      Boolean status, UserSummary blockingMember, UserSummary blockedMember) {
    return new UserBlockResponse(
        status,
        new BlockMemberInfo(blockingMember.id(), blockingMember.name()),
        new BlockMemberInfo(blockedMember.id(), blockedMember.name()));
  }
}
