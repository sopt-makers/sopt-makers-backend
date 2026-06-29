package org.sopt.makers.domain.official.corevalue;

public record CoreValue(
    Long id,
    Integer generationId,
    String value,
    String description,
    String detailDescription,
    String imageUrl,
    int displayOrder) {}
