package org.sopt.makers.domain.official.review;

public record ScrapedReviewArticle(
    String thumbnailUrl, String title, String description, String articleUrl, String platform) {}
