package org.sopt.makers.domain.playground.community.post;

import org.sopt.makers.domain.playground.community.CommunityPostSourceType;
import org.sopt.makers.domain.playground.community.CommunityPostTag;

public record PopularPost(
    CommunityPostSourceType sourceType,
    Long id,
    String title,
    PopularPostMember member,
    int hits,
    int likeCount,
    int commentCount,
    CommunityPostTag categoryTag) {

  public record PopularPostMember(Long id, String name, String profileImage) {}
}
