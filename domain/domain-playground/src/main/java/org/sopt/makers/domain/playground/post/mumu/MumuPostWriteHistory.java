package org.sopt.makers.domain.playground.post.mumu;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MumuPostWriteHistory(
    Long id, Long userId, LocalDate writtenDate, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static MumuPostWriteHistory create(Long userId, LocalDate writtenDate) {
    return new MumuPostWriteHistory(null, userId, writtenDate, null, null);
  }
}
