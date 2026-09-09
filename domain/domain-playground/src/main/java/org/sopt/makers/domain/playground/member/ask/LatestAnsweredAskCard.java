package org.sopt.makers.domain.playground.member.ask;

public record LatestAnsweredAskCard(
    Long receiverUserId,
    String receiverName,
    String receiverProfileImage,
    Long questionId,
    String content,
    AskLocation location) {}
