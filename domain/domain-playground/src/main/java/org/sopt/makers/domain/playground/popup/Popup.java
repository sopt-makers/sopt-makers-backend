package org.sopt.makers.domain.playground.popup;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Popup(
    Long id,
    LocalDate startDate,
    LocalDate endDate,
    String pcImageUrl,
    String mobileImageUrl,
    String linkUrl,
    Boolean openInNewTab,
    Boolean showOnlyToRecentGeneration,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
