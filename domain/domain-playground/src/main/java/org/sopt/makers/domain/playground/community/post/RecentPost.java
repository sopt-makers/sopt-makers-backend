package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;

/** totalVoteCount는 vote 도메인이 아직 이관되지 않아 항상 null이다(TODO). */
public record RecentPost(
    CommunityPostSourceType sourceType,
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    int likeCount,
    int commentCount,
    CommunityPostTag categoryTag,
    Integer totalVoteCount) {}
