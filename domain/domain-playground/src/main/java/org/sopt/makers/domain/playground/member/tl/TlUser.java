package org.sopt.makers.domain.playground.member.tl;

public record TlUser(
    Long id,
    Long memberUserId,
    Integer tlGeneration,
    ServiceType serviceType,
    String selfIntroduction,
    String competitionData) {}
