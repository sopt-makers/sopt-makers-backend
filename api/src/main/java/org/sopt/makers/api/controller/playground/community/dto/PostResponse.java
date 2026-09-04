package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;
import org.sopt.makers.api.common.util.RelativeTimeFormatter;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.post.PostFeedItem;

/**
 * comments/vote는 각각 comment/vote 도메인이 아직 이관되지 않아 항상 빈 값이다(TODO). 필드 자체는 기존 응답 스키마 동결을 위해
 * 유지한다.
 */
public record PostResponse(
    Long id,
    CommunityPostSourceType sourceType,
    MemberResponse member,
    Long writerId,
    Boolean isMine,
    Boolean isLiked,
    Integer likes,
    CommunityCategoryGroup categoryGroup,
    CommunityCategoryCode categoryCode,
    String categoryName,
    List<CommunityPostTag> tags,
    String title,
    String content,
    Integer hits,
    Integer commentCount,
    List<String> images,
    Boolean isBlindWriter,
    String sopticleUrl,
    AnonymousProfileResponse anonymousProfile,
    String createdAt,
    List<Object> comments,
    Object vote,
    Long meetingId) {

  public static PostResponse from(PostFeedItem item) {
    return new PostResponse(
        item.id(),
        item.sourceType(),
        MemberResponse.from(item.member()),
        item.writerId(),
        item.isMine(),
        item.isLiked(),
        item.likes(),
        item.categoryGroup(),
        item.categoryCode(),
        item.categoryName(),
        item.tags(),
        item.title(),
        item.content(),
        item.hits(),
        item.commentCount(),
        item.images(),
        item.isBlindWriter(),
        item.sopticleUrl(),
        AnonymousProfileResponse.from(item.anonymousProfile()),
        RelativeTimeFormatter.format(item.createdAt()),
        List.of(),
        null,
        item.meetingId());
  }
}
