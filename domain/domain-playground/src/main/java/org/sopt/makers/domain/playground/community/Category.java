package org.sopt.makers.domain.playground.community;

public record Category(
    Long id,
    CommunityCategoryCode code,
    CommunityCategoryGroup categoryGroup,
    String name,
    String content,
    Boolean hasAll,
    Boolean hasBlind,
    Boolean hasQuestion,
    Boolean isActive,
    Long parentId,
    Integer displayOrder) {}
