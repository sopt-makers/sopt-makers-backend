package org.sopt.makers.api.controller.playground.community.dto;

/** 게시글/댓글 작성 요청에 포함되는 멘션 푸시 알림 정보. CommentCommandService/CommunityPostCommandService에서 멘션 알림 발송에 연동된다. */
public record MentionRequest(Long[] userIds, String writerName, String webLink) {}
