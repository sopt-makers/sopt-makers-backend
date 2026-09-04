package org.sopt.makers.domain.playground.community.post;

import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;

public record PostDetail(
    Post post,
    Category category,
    Category parentCategory,
    CommunityMemberSummary member,
    boolean isMine,
    boolean isLiked,
    int likes,
    AnonymousProfile anonymousProfile) {}
