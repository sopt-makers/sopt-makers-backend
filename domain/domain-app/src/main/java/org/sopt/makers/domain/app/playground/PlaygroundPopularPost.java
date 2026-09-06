package org.sopt.makers.domain.app.playground;

public record PlaygroundPopularPost(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    int rank,
    String category,
    String title,
    String content,
    String webLink) {}
