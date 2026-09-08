package org.sopt.makers.storage.redis.playground.adapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.community.post.crew.CrewMeetingPost;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingClientPort;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingClientPort.CrewMeetingPage;
import org.sopt.makers.domain.playground.community.post.crew.port.CrewMeetingPostPort;
import org.sopt.makers.storage.redis.playground.cache.CachedCrewMeetingFeed;
import org.sopt.makers.storage.redis.playground.cache.CachedCrewMeetingPost;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewMeetingPostRepositoryAdapter implements CrewMeetingPostPort {

  private static final String CACHE_PREFIX = "community:free-feed:meeting:";
  private static final Duration CACHE_TTL = Duration.ofMinutes(10);
  private static final int CREW_POST_CACHE_TAKE = 50;
  private static final int PREFETCH_BUFFER_COUNT = 20;

  private final CrewMeetingClientPort crewMeetingClientPort;
  private final RedisTemplate<String, CachedCrewMeetingFeed> crewMeetingFeedRedisTemplate;

  private record CacheRebuildResult(CachedCrewMeetingFeed cache, boolean failed) {}

  @Override
  public CrewMeetingFeedPage getFeed(
      Long userId, LocalDateTime snapshotTime, int minimumRequiredCount) {
    return toFeedPage(
        getOrBuildCache(userId, snapshotTime, minimumRequiredCount, PREFETCH_BUFFER_COUNT));
  }

  @Override
  public CrewMeetingFeedPage getPreview(
      Long userId, LocalDateTime snapshotTime, int minimumRequiredCount) {
    return toFeedPage(getOrBuildCache(userId, snapshotTime, minimumRequiredCount, 0));
  }

  @Override
  public CrewMeetingFeedPage getPopularPreview(
      Long userId, LocalDateTime snapshotTime, LocalDateTime since, int maxPageCount) {
    CachedCrewMeetingFeed cache = readCache(userId, snapshotTime);

    if (containsOlderThanOrEqualToSince(cache.safePosts(), since) || !cache.hasMorePage()) {
      return toFeedPage(cache);
    }

    CacheRebuildResult rebuildResult = rebuildCacheUntil(userId, snapshotTime, since, maxPageCount);

    if (rebuildResult.failed()) {
      log.warn(
          "모임 인기글 캐시 재구성 실패. 기존 캐시를 유지합니다. userId: {}, snapshotTime: {}", userId, snapshotTime);
      return toFeedPage(fallbackWithoutMore(cache));
    }

    writeCache(userId, snapshotTime, rebuildResult.cache());
    return toFeedPage(rebuildResult.cache());
  }

  private CachedCrewMeetingFeed getOrBuildCache(
      Long userId, LocalDateTime snapshotTime, int minimumRequiredCount, int prefetchBufferCount) {
    CachedCrewMeetingFeed cache = readCache(userId, snapshotTime);

    if (cache.safePosts().size() >= minimumRequiredCount || !cache.hasMorePage()) {
      return cache;
    }

    int targetCount = minimumRequiredCount + prefetchBufferCount;
    CacheRebuildResult rebuildResult = rebuildCache(userId, snapshotTime, targetCount);

    if (rebuildResult.failed()) {
      log.warn("모임 피드 캐시 재구성 실패. 기존 캐시를 유지합니다. userId: {}, snapshotTime: {}", userId, snapshotTime);
      return fallbackWithoutMore(cache);
    }

    writeCache(userId, snapshotTime, rebuildResult.cache());
    return rebuildResult.cache();
  }

  private CacheRebuildResult rebuildCache(
      Long userId, LocalDateTime snapshotTime, int targetCount) {
    int page = 1;
    boolean hasMorePage = true;
    List<CachedCrewMeetingPost> cachedPosts = new ArrayList<>();

    while (cachedPosts.size() < targetCount && hasMorePage) {
      CrewMeetingPage crewPage = safeFetchPosts(userId, page);

      if (crewPage.fetchFailed()) {
        return new CacheRebuildResult(
            new CachedCrewMeetingFeed(normalize(cachedPosts), true), true);
      }

      for (CrewMeetingPost post : crewPage.posts()) {
        if (post.createdAt().isAfter(snapshotTime)) {
          continue;
        }
        cachedPosts.add(CachedCrewMeetingPost.from(post));
      }

      hasMorePage = crewPage.hasNextPage();
      page++;
    }

    return new CacheRebuildResult(
        new CachedCrewMeetingFeed(normalize(cachedPosts), hasMorePage), false);
  }

  private CacheRebuildResult rebuildCacheUntil(
      Long userId, LocalDateTime snapshotTime, LocalDateTime since, int maxPageCount) {
    int page = 1;
    boolean hasMorePage = true;
    List<CachedCrewMeetingPost> cachedPosts = new ArrayList<>();

    while (page <= maxPageCount && hasMorePage) {
      CrewMeetingPage crewPage = safeFetchPosts(userId, page);

      if (crewPage.fetchFailed()) {
        return new CacheRebuildResult(
            new CachedCrewMeetingFeed(normalize(cachedPosts), true), true);
      }

      boolean reachedOlderThanSince = false;

      for (CrewMeetingPost post : crewPage.posts()) {
        if (post.createdAt().isAfter(snapshotTime)) {
          continue;
        }

        cachedPosts.add(CachedCrewMeetingPost.from(post));

        if (post.createdAt().isBefore(since)) {
          reachedOlderThanSince = true;
        }
      }

      hasMorePage = crewPage.hasNextPage();

      if (reachedOlderThanSince) {
        break;
      }

      page++;
    }

    return new CacheRebuildResult(
        new CachedCrewMeetingFeed(normalize(cachedPosts), hasMorePage), false);
  }

  private boolean containsOlderThanOrEqualToSince(
      List<CachedCrewMeetingPost> cachedPosts, LocalDateTime since) {
    return cachedPosts.stream().anyMatch(post -> !post.createdAt().isAfter(since));
  }

  private List<CachedCrewMeetingPost> normalize(List<CachedCrewMeetingPost> cachedPosts) {
    Map<Long, CachedCrewMeetingPost> deduplicatedPosts = new LinkedHashMap<>();

    for (CachedCrewMeetingPost cachedPost : cachedPosts) {
      deduplicatedPosts.put(cachedPost.id(), cachedPost);
    }

    return deduplicatedPosts.values().stream()
        .sorted(
            Comparator.comparing(CachedCrewMeetingPost::createdAt)
                .reversed()
                .thenComparing(CachedCrewMeetingPost::id, Comparator.reverseOrder()))
        .toList();
  }

  private CachedCrewMeetingFeed readCache(Long userId, LocalDateTime snapshotTime) {
    String cacheKey = generateCacheKey(userId, snapshotTime);
    CachedCrewMeetingFeed cached = crewMeetingFeedRedisTemplate.opsForValue().get(cacheKey);
    return cached == null ? CachedCrewMeetingFeed.empty() : cached;
  }

  private void writeCache(Long userId, LocalDateTime snapshotTime, CachedCrewMeetingFeed cache) {
    String cacheKey = generateCacheKey(userId, snapshotTime);

    try {
      crewMeetingFeedRedisTemplate.opsForValue().set(cacheKey, cache, CACHE_TTL);
    } catch (Exception exception) {
      log.warn("모임 피드 캐시 저장 실패. cacheKey: {}", cacheKey, exception);
    }
  }

  private String generateCacheKey(Long userId, LocalDateTime snapshotTime) {
    return CACHE_PREFIX + userId + ":" + snapshotTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  private CrewMeetingPage safeFetchPosts(Long userId, int page) {
    try {
      return crewMeetingClientPort.fetchPosts(userId, page, CREW_POST_CACHE_TAKE);
    } catch (Exception exception) {
      log.warn("모임 피드 조회 실패. userId: {}, page: {}", userId, page, exception);
      return CrewMeetingPage.failed();
    }
  }

  private CachedCrewMeetingFeed fallbackWithoutMore(CachedCrewMeetingFeed cache) {
    return new CachedCrewMeetingFeed(cache.safePosts(), false);
  }

  private CrewMeetingFeedPage toFeedPage(CachedCrewMeetingFeed cache) {
    List<CrewMeetingPost> posts =
        cache.safePosts().stream().map(CachedCrewMeetingPost::toDomain).toList();
    return new CrewMeetingFeedPage(posts, cache.hasMorePage());
  }
}
