package org.sopt.makers.api.controller.playground.member.dto;

import org.sopt.makers.domain.playground.member.profile.MemberSummary;

public record MemberBlockResponse(
    Boolean status, BlockMemberInfo blockingMember, BlockMemberInfo blockedMember) {

  public record BlockMemberInfo(Long id, String name) {}

  public static MemberBlockResponse of(
      Boolean status, MemberSummary blockingMember, MemberSummary blockedMember) {
    return new MemberBlockResponse(
        status,
        new BlockMemberInfo(blockingMember.id(), blockingMember.name()),
        new BlockMemberInfo(blockedMember.id(), blockedMember.name()));
  }
}
