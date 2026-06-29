package org.sopt.makers.domain.official.generation;

public record Generation(
    Integer id,
    String name,
    String headerImage,
    String recruitHeaderImage,
    String homeHeaderImage,
    BrandingColor brandingColor) {}
