package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.comment.CommentThread;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.vote.VoteResult;

/** 게시글 목록/피드에서 한 건을 표현하는 read model. 모임(MEETING) 게시글은 댓글이 크루 도메인 소관이라 comments가 항상 빈 목록이다. */
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
    List<CommentThread> comments,
    List<String> images,
    boolean isBlindWriter,
    String sopticleUrl,
    AnonymousProfile anonymousProfile,
    LocalDateTime createdAt,
    Long meetingId,
    VoteResult vote) {}
