package org.sopt.makers.domain.official.soptstory;

public record ScrapedArticle(
    String thumbnailUrl, String title, String description, String articleUrl, String platform) {}
