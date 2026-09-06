package org.sopt.makers.api.controller.playground.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileResponse.MemberQuestionPreviewResponse;
import org.sopt.makers.domain.playground.member.ask.AskPreview;
import org.sopt.makers.domain.playground.member.profile.MemberProfileListItem;
import org.sopt.makers.domain.playground.member.profile.MemberProfileListResult;

public record MemberAllProfileResponse(
    @Schema(required = true) List<MemberProfileResponse> members,
    @Schema(required = true) Boolean hasNext,
    @Schema(required = true) Integer totalMembersCount) {

  public static MemberAllProfileResponse from(MemberProfileListResult result) {
    List<MemberProfileResponse> members =
        result.members().stream().map(MemberAllProfileResponse::toMemberProfileResponse).toList();
    return new MemberAllProfileResponse(members, result.hasNext(), result.totalCount());
  }

  private static MemberProfileResponse toMemberProfileResponse(MemberProfileListItem item) {
    return MemberProfileResponse.from(item.user(), item.isCoffeeChatActivate(), toPreview(item.questionPreview()));
  }

  private static MemberQuestionPreviewResponse toPreview(AskPreview preview) {
    return preview == null ? null : new MemberQuestionPreviewResponse(preview.questionId(), preview.content());
  }
}
