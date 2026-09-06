package org.sopt.makers.storage.redis.app.soptamp.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;
import org.sopt.makers.domain.app.soptamp.rank.port.RankCachePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankCacheAdapter implements RankCachePort {

  private static final String KEY_PREFIX = "soptamp:score:";

  private final StringRedisTemplate stringRedisTemplate;

  @Value("${sopt.current.generation}")
  private Long currentGeneration;

  @Override
  public List<RankedScore> getRanking() {
    try {
      Set<TypedTuple<String>> tuples =
          stringRedisTemplate.opsForZSet().reverseRangeWithScores(scoreKey(), 0, -1);
      if (tuples == null) {
        return List.of();
      }
      return tuples.stream().map(RankCacheAdapter::toRankedScore).toList();
    } catch (Exception e) {
      log.warn("솝탬프 랭킹 캐시 조회 실패 - key={}", scoreKey(), e);
      return List.of();
    }
  }

  @Override
  public void putAll(List<RankedScore> scores) {
    if (scores.isEmpty()) {
      return;
    }
    try {
      Set<TypedTuple<String>> tuples =
          scores.stream()
              .map(score -> TypedTuple.of(String.valueOf(score.userId()), (double) score.score()))
              .collect(Collectors.toSet());
      stringRedisTemplate.opsForZSet().add(scoreKey(), tuples);
    } catch (Exception e) {
      log.warn("솝탬프 랭킹 캐시 적재 실패 - key={}, size={}", scoreKey(), scores.size(), e);
    }
  }

  @Override
  public void updateScore(Long userId, long score) {
    stringRedisTemplate.opsForZSet().add(scoreKey(), String.valueOf(userId), score);
  }

  @Override
  public void removeScore(Long userId) {
    stringRedisTemplate.opsForZSet().remove(scoreKey(), String.valueOf(userId));
  }

  @Override
  public void clearScores() {
    stringRedisTemplate.delete(scoreKey());
  }

  private String scoreKey() {
    return KEY_PREFIX + currentGeneration;
  }

  private static RankedScore toRankedScore(TypedTuple<String> tuple) {
    Double score = tuple.getScore();
    return new RankedScore(Long.valueOf(tuple.getValue()), score == null ? 0L : score.longValue());
  }
}
