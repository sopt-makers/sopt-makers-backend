package org.sopt.makers.domain.playground.community.post;

import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;

public record PostFeedResult(
    CommunityPostListCategory category,
    boolean hasNext,
    String nextCursor,
    List<PostFeedItem> items) {}
