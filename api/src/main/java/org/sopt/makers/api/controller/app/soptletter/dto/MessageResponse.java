package org.sopt.makers.api.controller.app.soptletter.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;

public record MessageResponse(
    Long messageId,
    Long topicId,
    String authorNickname,
    String content,
    String colorCode,
    Double rotationDegree,
    String shapeType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int likeCount,
    boolean likedByMe,
    boolean mine) {

  public static MessageResponse of(SoptLetterView view) {
    SoptLetter letter = view.letter();
    return new MessageResponse(
        letter.id(),
        letter.topicId(),
        view.authorNickname(),
        letter.message(),
        letter.color() == null ? null : letter.color().getHexCode(),
        letter.degree(),
        letter.shapeType() == null ? null : letter.shapeType().name(),
        letter.createdAt(),
        letter.updatedAt(),
        letter.likeCount(),
        view.likedByMe(),
        view.mine());
  }
}
