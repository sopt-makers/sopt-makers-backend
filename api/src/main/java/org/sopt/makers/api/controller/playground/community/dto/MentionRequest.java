package org.sopt.makers.api.controller.playground.community.dto;

/**
 * 게시글 작성/수정 요청에 포함되는 멘션 푸시 알림 정보. 알림 발송 연동이 아직 이관되지 않아 요청 스키마 동결을 위해 필드만 유지하며, 실제 알림 발송에는 아직
 * 연동하지 않는다. (TODO: 알림 발송 Client Port 이관 후 연동)
 */
public record MentionRequest(Long[] userIds, String writerName, String webLink) {}
