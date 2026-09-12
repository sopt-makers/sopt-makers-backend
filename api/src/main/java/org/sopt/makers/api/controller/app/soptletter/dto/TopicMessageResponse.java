package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;

public record TopicMessageResponse(
    @Schema(description = "메시지 아이디", example = "120") Long messageId,
    @Schema(description = "작성자 익명 닉네임", example = "그윽한 떡볶이") String authorNickname,
    @Schema(description = "목록에 띄울 미리보기 내용. 50자를 넘으면 잘린다", example = "다들 고생 많으셨어요")
        String previewContent,
    @Schema(description = "쪽지 배경 색상 코드", example = "#FFE1E1") String colorCode,
    @Schema(description = "쪽지를 기울여 그릴 각도", example = "-3.5") Double rotationDegree,
    @Schema(description = "쪽지 모양 종류", example = "SQUARE") String shapeType,
    @Schema(description = "작성 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
    @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
    @Schema(description = "받은 좋아요 수", example = "12") int likeCount,
    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "false") boolean likedByMe,
    @Schema(description = "내가 쓴 메시지인지 여부", example = "true") boolean mine) {

  private static final int PREVIEW_CONTENT_LENGTH = 50;

  public static TopicMessageResponse of(SoptLetterView view) {
    SoptLetter letter = view.letter();
    return new TopicMessageResponse(
        letter.id(),
        view.authorNickname(),
        toPreview(letter.message()),
        letter.colorHexCode(),
        letter.degree(),
        letter.shapeTypeName(),
        letter.createdAt(),
        letter.updatedAt(),
        letter.likeCount(),
        view.likedByMe(),
        view.mine());
  }

  private static String toPreview(String content) {
    if (content == null || content.length() <= PREVIEW_CONTENT_LENGTH) {
      return content;
    }
    return content.substring(0, PREVIEW_CONTENT_LENGTH);
  }
}
