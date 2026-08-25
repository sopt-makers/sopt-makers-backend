package org.sopt.makers.domain.playground.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Project(
    Long id,
    Long writerId,
    String name,
    Integer generation,
    String category,
    LocalDate startAt,
    LocalDate endAt,
    List<String> serviceType,
    Boolean isAvailable,
    Boolean isFounding,
    String summary,
    String detail,
    String logoImage,
    String thumbnailImage,
    List<String> images,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
