package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;

/**
 * 게시글 목록/피드에서 한 건을 표현하는 read model. commentCount는 comment 도메인이 아직 이관되지 않아 항상 0으로
 * 채워진다(TODO).
 */
public record PostFeedItem(
    CommunityPostSourceType sourceType,
    Long id,
    CommunityMemberSummary member,
    Long writerId,
    boolean isMine,
    boolean isLiked,
    int likes,
    CommunityCategoryGroup categoryGroup,
    CommunityCategoryCode categoryCode,
    String categoryName,
    List<CommunityPostTag> tags,
    String title,
    String content,
    int hits,
    int commentCount,
    List<String> images,
    boolean isBlindWriter,
    String sopticleUrl,
    AnonymousProfile anonymousProfile,
    LocalDateTime createdAt,
    Long meetingId) {}
