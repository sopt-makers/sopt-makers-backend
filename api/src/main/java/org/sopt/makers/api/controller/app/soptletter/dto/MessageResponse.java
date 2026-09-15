package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;

public record MessageResponse(
    @Schema(description = "메시지 아이디", example = "120") Long messageId,
    @Schema(description = "메시지가 달린 주제 아이디", example = "1") Long topicId,
    @Schema(description = "작성자 익명 닉네임", example = "그윽한 떡볶이") String authorNickname,
    @Schema(description = "메시지 전체 내용", example = "다들 고생 많으셨어요") String content,
    @Schema(description = "쪽지 배경 색상 코드", example = "#FFE1E1") String colorCode,
    @Schema(description = "쪽지를 기울여 그릴 각도", example = "-3.5") Double rotationDegree,
    @Schema(description = "쪽지 모양 종류", example = "SQUARE") String shapeType,
    @Schema(description = "작성 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
    @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
    @Schema(description = "받은 좋아요 수", example = "12") int likeCount,
    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "false") boolean likedByMe,
    @Schema(description = "내가 쓴 메시지인지 여부", example = "true") boolean mine) {

  public static MessageResponse of(SoptLetterView view) {
    SoptLetter letter = view.letter();
    return new MessageResponse(
        letter.id(),
        letter.topicId(),
        view.authorNickname(),
        letter.message(),
        letter.colorHexCode(),
        letter.degree(),
        letter.shapeTypeName(),
        letter.createdAt(),
        letter.updatedAt(),
        letter.likeCount(),
        view.likedByMe(),
        view.mine());
  }
}
