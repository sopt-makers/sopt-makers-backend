package org.sopt.makers.api.controller.app.soptletter.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;

public record TopicMessageResponse(
    Long messageId,
    String authorNickname,
    String previewContent,
    String colorCode,
    Double rotationDegree,
    String shapeType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int likeCount,
    boolean likedByMe,
    boolean mine) {

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
