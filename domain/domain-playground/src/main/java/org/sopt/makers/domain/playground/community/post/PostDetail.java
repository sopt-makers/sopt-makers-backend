package org.sopt.makers.domain.playground.community.post;

import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.vote.VoteResult;

public record PostDetail(
    Post post,
    Category category,
    Category parentCategory,
    CommunityMemberSummary member,
    boolean isMine,
    boolean isLiked,
    int likes,
    AnonymousProfile anonymousProfile,
    VoteResult vote) {}
