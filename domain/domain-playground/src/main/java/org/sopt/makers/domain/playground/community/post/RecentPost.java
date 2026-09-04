package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;

/** totalVoteCount는 게시글에 투표가 없으면 null이다. */
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
