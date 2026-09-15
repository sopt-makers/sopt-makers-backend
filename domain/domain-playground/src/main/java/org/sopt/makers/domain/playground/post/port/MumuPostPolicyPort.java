package org.sopt.makers.domain.playground.post.port;

import java.time.LocalDate;

/** 공통 게시글 도메인이 Crew의 무무 정책을 직접 의존하지 않고 사용하는 경계. */
public interface MumuPostPolicyPort {

  String getCurrentText();

  boolean hasWritten(Long userId, LocalDate writtenDate);

  void recordWrittenIfAbsent(Long userId, LocalDate writtenDate);
}
