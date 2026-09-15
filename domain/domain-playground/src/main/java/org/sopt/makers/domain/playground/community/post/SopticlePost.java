package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;

public record SopticlePost(
    Long id,
    CommunityMemberSummary member,
    LocalDateTime createdAt,
    String title,
    String content,
    List<String> images,
    String sopticleUrl) {}
