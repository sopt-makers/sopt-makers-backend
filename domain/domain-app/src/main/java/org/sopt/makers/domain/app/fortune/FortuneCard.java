package org.sopt.makers.domain.app.fortune;

public record FortuneCard(
    Long id, String name, String description, String imageUrl, String imageColorCode) {}
