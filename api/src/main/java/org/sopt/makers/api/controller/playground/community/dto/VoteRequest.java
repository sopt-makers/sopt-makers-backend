package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;

/**
 * 커뮤니티 글 작성 요청에 포함되는 투표 생성 정보. Vote 도메인이 아직 이관되지 않아 요청 스키마 동결을 위해 필드만 유지하며, 실제 투표 생성 로직에는 아직
 * 연동하지 않는다. (TODO: Vote 도메인 이관 후 연동)
 */
public record VoteRequest(boolean isMultiple, List<String> voteOptions) {}
