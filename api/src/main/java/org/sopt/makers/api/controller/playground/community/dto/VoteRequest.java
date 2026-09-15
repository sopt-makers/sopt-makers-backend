package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;

/** 커뮤니티 글 작성 요청에 포함되는 투표 생성 정보. */
public record VoteRequest(boolean isMultiple, List<String> voteOptions) {}
