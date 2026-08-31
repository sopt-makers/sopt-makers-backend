package org.sopt.makers.domain.crew.notice;

import java.time.LocalDateTime;

public record Notice(
    Long id,
    String title,
    String subTitle,
    String contents,
    LocalDateTime createdDate,
    LocalDateTime exposeStartDate,
    LocalDateTime exposeEndDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Notice create(
      String title,
      String subTitle,
      String contents,
      LocalDateTime createdDate,
      LocalDateTime exposeStartDate,
      LocalDateTime exposeEndDate) {
    return new Notice(
        null, title, subTitle, contents, createdDate, exposeStartDate, exposeEndDate, null, null);
  }
}
