package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 익명 사용자 멘션 요청. 알림 발송 연동이 아직 이관되지 않아 요청 스키마 동결을 위해 필드만 유지한다. */
public record AnonymousMentionRequest(
    @Schema(description = "익명 멘션된 사용자들의 익명 닉네임 배열", example = "[\"오너십있는 츄러스\", \"도전하는 빙수\"]") String[] anonymousNicknames) {}
