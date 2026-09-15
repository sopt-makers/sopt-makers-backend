package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;

public record CommunityDbCursor(LocalDateTime createdAt, Long postId) {}
