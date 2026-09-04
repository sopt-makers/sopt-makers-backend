package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostCommandService.CreatePostCommand;
import org.sopt.makers.domain.playground.community.vote.service.VoteCommandService;

public record PostSaveRequest(
    @Schema(required = true) @NotNull(message = "카테고리 코드는 필수 입력값입니다.") CommunityCategoryCode categoryCode,
    String title,
    @Schema(required = true) @NotBlank(message = "게시글 본문은 공백일 수 없습니다.") String content,
    @Schema(required = true) @NotNull(message = "익명글 여부 필드는 필수 입력값입니다.") Boolean isBlindWriter,
    @Schema(required = true) @NotNull(message = "이미지 필드는 필수 필드입니다.") List<String> images,
    String link,
    VoteRequest vote,
    MentionRequest mention) {

  public CreatePostCommand toCommand() {
    return new CreatePostCommand(categoryCode, title, content, isBlindWriter, images, link, toVoteCommand());
  }

  private VoteCommandService.CreateVoteCommand toVoteCommand() {
    return vote == null ? null : new VoteCommandService.CreateVoteCommand(vote.isMultiple(), vote.voteOptions());
  }
}
