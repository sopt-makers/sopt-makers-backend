package org.sopt.makers.domain.app.soptletter;

import java.time.LocalDateTime;
import java.util.Objects;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure;

public record SoptLetter(
    Long id,
    Long authorProfileId,
    Long topicId,
    Double degree,
    String message,
    SoptLetterColor color,
    SoptLetterShapeType shapeType,
    int likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public SoptLetter withMessage(String newMessage, LocalDateTime newUpdatedAt) {
    return new SoptLetter(
        id,
        authorProfileId,
        topicId,
        degree,
        newMessage,
        color,
        shapeType,
        likeCount,
        createdAt,
        newUpdatedAt);
  }

  public String colorHexCode() {
    return color == null ? null : color.getHexCode();
  }

  public String shapeTypeName() {
    return shapeType == null ? null : shapeType.name();
  }

  public boolean isAuthor(Long profileId) {
    return Objects.equals(authorProfileId, profileId);
  }

  public boolean isInTopic(Long topicId) {
    return Objects.equals(this.topicId, topicId);
  }

  public void validateInTopic(Long topicId) {
    if (!isInTopic(topicId)) {
      throw new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER);
    }
  }

  public void validateAuthor(Long profileId) {
    if (!isAuthor(profileId)) {
      throw new SoptLetterException(SoptLetterFailure.FORBIDDEN_SOPT_LETTER);
    }
  }
}
