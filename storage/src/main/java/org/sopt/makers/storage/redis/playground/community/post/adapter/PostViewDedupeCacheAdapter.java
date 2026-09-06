package org.sopt.makers.storage.redis.playground.community.post.adapter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.community.post.port.PostViewDedupePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 키 포맷 {@code post:view:{yyyy-MM-dd}:{userId}:{postId}}, TTL 24h. Redis {@code SETNX}(setIfAbsent)로
 * 조회-후-저장 사이의 경쟁 상태 없이 원자적으로 "오늘 처음 조회했는지"를 판정한다. Redis 장애 시에는 중복방지를 포기하고
 * 항상 조회수 증가를 허용한다(가용성 우선, Top-50 캐시 등 다른 Redis 어댑터와 동일한 fail-open 정책).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewDedupeCacheAdapter implements PostViewDedupePort {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final Duration TTL = Duration.ofHours(24);
  private static final String VIEWED_MARK = "1";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public boolean markAsViewedIfAbsent(Long userId, Long postId) {
    String key = buildKey(userId, postId);
    try {
      Boolean isFirstViewToday = stringRedisTemplate.opsForValue().setIfAbsent(key, VIEWED_MARK, TTL);
      return Boolean.TRUE.equals(isFirstViewToday);
    } catch (Exception exception) {
      log.warn("게시글 조회수 중복방지 캐시 처리 실패. key={}", key, exception);
      return true;
    }
  }

  private String buildKey(Long userId, Long postId) {
    return "post:view:%s:%d:%d".formatted(LocalDate.now().format(DATE_FORMATTER), userId, postId);
  }
}
