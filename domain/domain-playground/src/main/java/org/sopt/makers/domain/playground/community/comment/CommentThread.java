package org.sopt.makers.domain.playground.community.comment;

import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;

/** 댓글 목록/피드에서 한 건을 표현하는 read model. member/anonymousProfile 중 하나만 채워진다(익명 여부에 따름). */
public record CommentThread(
    Comment comment,
    CommunityMemberSummary member,
    AnonymousProfile anonymousProfile,
    boolean isMine,
    boolean isLiked,
    int likeCount) {}
