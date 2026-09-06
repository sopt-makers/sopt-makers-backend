package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.api.controller.playground.user.dto.UserProfileResponse.MemberQuestionPreviewResponse;
import org.sopt.makers.domain.playground.member.ask.AskPreview;
import org.sopt.makers.domain.playground.member.profile.MemberProfileListItem;
import org.sopt.makers.domain.playground.member.profile.UserProfileListResult;

public record UserAllProfileResponse(
    @Schema(required = true) List<UserProfileResponse> members,
    @Schema(required = true) Boolean hasNext,
    @Schema(required = true) Integer totalMembersCount) {

  public static UserAllProfileResponse from(UserProfileListResult result) {
    List<UserProfileResponse> members =
        result.members().stream().map(UserAllProfileResponse::toMemberProfileResponse).toList();
    return new UserAllProfileResponse(members, result.hasNext(), result.totalCount());
  }

  private static UserProfileResponse toMemberProfileResponse(MemberProfileListItem item) {
    return UserProfileResponse.from(item.user(), item.isCoffeeChatActivate(), toPreview(item.questionPreview()));
  }

  private static MemberQuestionPreviewResponse toPreview(AskPreview preview) {
    return preview == null ? null : new MemberQuestionPreviewResponse(preview.questionId(), preview.content());
  }
}
