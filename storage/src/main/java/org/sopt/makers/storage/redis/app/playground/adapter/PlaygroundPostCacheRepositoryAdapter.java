package org.sopt.makers.storage.redis.app.playground.adapter;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostCacheRepositoryPort;
import org.sopt.makers.storage.redis.app.playground.cache.CachedPlaygroundPopularPosts;
import org.sopt.makers.storage.redis.app.playground.cache.CachedPlaygroundRecentPosts;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PlaygroundPostCacheRepositoryAdapter implements PlaygroundPostCacheRepositoryPort {

  private static final String RECENT_POSTS_KEY = "playground:posts:recent";
  private static final String POPULAR_POSTS_KEY = "playground:posts:popular";
  private static final String RECENT_LOCK_KEY = "playground:posts:recent_refresh_lock";
  private static final String POPULAR_LOCK_KEY = "playground:posts:popular_refresh_lock";
  private static final String LOCKED_STATUS = "LOCKED";
  private static final Duration CACHE_TTL = Duration.ofHours(24);
  private static final Duration LOCK_TTL = Duration.ofMinutes(5);

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public Optional<List<PlaygroundRecentPost>> getCachedRecentPosts() {
    return Optional.ofNullable(stringRedisTemplate.opsForValue().get(RECENT_POSTS_KEY))
        .map(json -> objectMapper.readValue(json, CachedPlaygroundRecentPosts.class))
        .map(CachedPlaygroundRecentPosts::toDomain);
  }

  @Override
  public Optional<List<PlaygroundPopularPost>> getCachedPopularPosts() {
    return Optional.ofNullable(stringRedisTemplate.opsForValue().get(POPULAR_POSTS_KEY))
        .map(json -> objectMapper.readValue(json, CachedPlaygroundPopularPosts.class))
        .map(CachedPlaygroundPopularPosts::toDomain);
  }

  @Override
  public void setCachedRecentPosts(List<PlaygroundRecentPost> posts) {
    String json = objectMapper.writeValueAsString(CachedPlaygroundRecentPosts.from(posts));
    stringRedisTemplate.opsForValue().set(RECENT_POSTS_KEY, json, CACHE_TTL);
  }

  @Override
  public void setCachedPopularPosts(List<PlaygroundPopularPost> posts) {
    String json = objectMapper.writeValueAsString(CachedPlaygroundPopularPosts.from(posts));
    stringRedisTemplate.opsForValue().set(POPULAR_POSTS_KEY, json, CACHE_TTL);
  }

  @Override
  public boolean tryLockRecentRefresh() {
    return tryLock(RECENT_LOCK_KEY);
  }

  @Override
  public boolean tryLockPopularRefresh() {
    return tryLock(POPULAR_LOCK_KEY);
  }

  private boolean tryLock(String lockKey) {
    Boolean acquired =
        stringRedisTemplate.opsForValue().setIfAbsent(lockKey, LOCKED_STATUS, LOCK_TTL);
    return Boolean.TRUE.equals(acquired);
  }
}
