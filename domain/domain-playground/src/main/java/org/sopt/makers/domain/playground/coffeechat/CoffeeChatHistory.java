package org.sopt.makers.domain.playground.coffeechat;

import java.time.LocalDateTime;

public record CoffeeChatHistory(
    Long id, Long receiverId, Long senderId, String requestContent, LocalDateTime createdAt) {}
