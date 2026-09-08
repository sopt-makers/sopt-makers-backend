package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.post.PopularPost.PopularPostMember;

public record MemberSummaryResponse(Long id, String name, String profileImage) {

  public static MemberSummaryResponse from(PopularPostMember member) {
    return member == null
        ? null
        : new MemberSummaryResponse(member.id(), member.name(), member.profileImage());
  }
}
