package org.sopt.makers.api.controller.admin.crew.mumu.dto;

import java.time.LocalDateTime;

public enum MumuTextStatus {
  ACTIVE,
  SCHEDULED,
  ENDED;

  public static MumuTextStatus from(
      LocalDateTime now, LocalDateTime startDate, LocalDateTime endDate) {
    if (!now.isBefore(startDate) && now.isBefore(endDate)) {
      return ACTIVE;
    }
    return now.isBefore(startDate) ? SCHEDULED : ENDED;
  }
}
