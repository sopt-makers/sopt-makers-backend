package org.sopt.makers.api.controller.playground.community.dto;

/**
 * 게시글/댓글 작성 요청에 포함되는 멘션 푸시 알림 정보. 댓글 작성 시에는 CommentCommandService에서 멘션 알림 발송에 연동된다. 게시글
 * 작성/수정 시 멘션 알림 발송은 아직 연동되지 않아 요청 스키마 동결을 위해 필드만 유지한다.
 */
public record MentionRequest(Long[] userIds, String writerName, String webLink) {}
