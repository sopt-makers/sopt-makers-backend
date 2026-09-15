package org.sopt.makers.domain.playground.community.post.port;

/** 게시글 조회수 어뷰징 방지용 하루 단위(24h) 중복조회 게이트. */
public interface PostViewDedupePort {

  /** 오늘 이 (userId, postId) 조합을 처음 조회한 경우에만 true를 반환하고, 동시에 조회 기록을 남긴다. */
  boolean markAsViewedIfAbsent(Long userId, Long postId);
}
