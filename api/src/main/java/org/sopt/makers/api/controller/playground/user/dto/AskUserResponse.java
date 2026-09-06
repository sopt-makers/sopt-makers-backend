package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.playground.member.ask.AskTargetMember;

@Schema(description = "질문 대상 멤버 응답 DTO")
public record AskUserResponse(
    @Schema(description = "질문 대상 멤버 목록") List<QuestionTargetMember> members) {

  @Schema(description = "질문 대상 멤버 정보")
  public record QuestionTargetMember(
      @Schema(description = "멤버 ID") Long id,
      @Schema(description = "멤버 이름") String name,
      @Schema(description = "프로필 이미지 URL") String profileImage,
      @Schema(description = "소개") String introduction,
      @Schema(description = "최근 활동 정보") AskMemberSoptActivityResponse latestActivity,
      @Schema(description = "커리어 정보") AskMemberCareerResponse career,
      @Schema(description = "답변 보장 여부", example = "true") Boolean isAnswerGuaranteed) {}

  public record AskMemberSoptActivityResponse(Integer generation, String part, String team) {}

  public record AskMemberCareerResponse(String companyName, String title) {}

  public static AskUserResponse from(List<AskTargetMember> targets) {
    List<QuestionTargetMember> members =
        targets.stream()
            .map(
                target ->
                    new QuestionTargetMember(
                        target.user().id(),
                        target.user().profile().name(),
                        target.user().profile().profileImage(),
                        target.user().profile().introduction(),
                        new AskMemberSoptActivityResponse(
                            target.latestActivity().generation(),
                            target.latestActivity().part() == null
                                ? null
                                : target.latestActivity().part().getName(),
                            target.latestActivity().team() == null
                                ? null
                                : target.latestActivity().team().getDisplayName()),
                        target.career() == null
                            ? null
                            : new AskMemberCareerResponse(
                                target.career().companyName(), target.career().title()),
                        true))
            .toList();
    return new AskUserResponse(members);
  }
}
