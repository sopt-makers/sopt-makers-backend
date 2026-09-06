package org.sopt.makers.domain.playground.coffeechat;

public record CoffeeChatReview(
    Long id,
    Long reviewerId,
    Long coffeeChatId,
    String anonymousProfileImageUrl,
    String nickname,
    String content) {}
