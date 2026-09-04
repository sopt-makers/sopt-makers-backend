package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.playground.community.comment.service.CommentCommandService.CreateCommentCommand;

/** webLink/mention/anonymousMention은 멘션 알림 발송 연동이 아직 이관되지 않아 요청 스키마 동결을 위해 필드만 유지한다. */
public record CommentSaveRequest(
    @Schema(required = true) @NotBlank(message = "댓글 내용은 공백일 수 없습니다.") String content,
    @Schema(required = true) @NotNull(message = "익명 댓글 여부 필드는 필수 입력값입니다.") Boolean isBlindWriter,
    @Schema(required = true) @NotNull(message = "답글 여부 필드는 필수 입력값입니다.") Boolean isChildComment,
    @Schema(required = true) @NotNull(message = "webLink는 필수 입력값입니다.") String webLink,
    Long parentCommentId,
    MentionRequest mention,
    AnonymousMentionRequest anonymousMention) {

  public CreateCommentCommand toCommand() {
    String[] anonymousNicknames = anonymousMention == null ? null : anonymousMention.anonymousNicknames();
    return new CreateCommentCommand(content, isBlindWriter, isChildComment, parentCommentId, anonymousNicknames);
  }
}
