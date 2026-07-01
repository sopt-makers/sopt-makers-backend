package org.sopt.makers.domain.official.member;

public record Member(
    Long id,
    Integer generationId,
    MemberRole role,
    String name,
    String affiliation,
    String introduction,
    String profileImageUrl,
    SnsLinks snsLinks) {}
