package org.sopt.makers.domain.playground.community.post.crew;

import java.time.LocalDateTime;
import java.util.List;

/** 외부 Crew 모임 게시판의 글 한 건을 나타내는 domain 모델. */
public record CrewMeetingPost(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    List<String> images,
    Long writerId,
    Long writerOrgId,
    String writerName,
    String writerProfileImage,
    String writerPart,
    int writerGeneration,
    int likeCount,
    boolean isLiked,
    int viewCount,
    int commentCount,
    Long meetingId) {}
