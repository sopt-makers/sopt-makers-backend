package org.sopt.makers.domain.crew.mumu;

import static org.sopt.makers.domain.crew.mumu.exception.MumuFailure.INVALID_MUMU_TEXT_PERIOD;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.mumu.exception.MumuException;

public record MumuText(
    Long id,
    String text,
    String category,
    LocalDateTime showStartDate,
    LocalDateTime showEndDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public MumuText {
    if (text == null
        || text.isBlank()
        || category == null
        || category.isBlank()
        || showStartDate == null
        || showEndDate == null
        || !showStartDate.isBefore(showEndDate)) {
      throw new MumuException(INVALID_MUMU_TEXT_PERIOD);
    }
  }

  public static MumuText create(
      String text, String category, LocalDateTime showStartDate, LocalDateTime showEndDate) {
    return new MumuText(null, text.trim(), category.trim(), showStartDate, showEndDate, null, null);
  }

  public MumuText update(
      String text, String category, LocalDateTime showStartDate, LocalDateTime showEndDate) {
    return new MumuText(
        id, text.trim(), category.trim(), showStartDate, showEndDate, createdAt, updatedAt);
  }

  public boolean isActiveAt(LocalDateTime now) {
    return !now.isBefore(showStartDate) && now.isBefore(showEndDate);
  }
}
