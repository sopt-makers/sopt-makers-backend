package org.sopt.makers.storage.config;

import java.time.Duration;
import org.sopt.makers.storage.redis.playground.cache.CachedCrewMeetingFeed;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileCard;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileRanking;
import org.sopt.makers.storage.redis.user.cache.CachedUserProfile;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

/**
 * {@code @EnableCaching}과 {@code CacheManager}를 이 클래스에 격리한다({@code @Cacheable} 등 애노테이션 기반 캐싱을 쓰는 곳만
 * 영향을 받도록 SoptMakersApplication 전역에 두지 않음).
 */
@EnableCaching
@Configuration
public class RedisConfig {

  private static final Duration DEFAULT_CACHE_TTL = Duration.ofMinutes(10);

  /**
   * {@code @Cacheable}/{@code @CacheEvict} 등이 사용할 기본 CacheManager. null 값은 캐싱하지 않는다(방어적으로 매 호출마다
   * 재조회).
   */
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaultConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(DEFAULT_CACHE_TTL)
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer.builder().build()));

    return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig).build();
  }

  @Bean
  public RedisTemplate<String, CachedUserProfile> userProfileRedisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, CachedUserProfile> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(
        new JacksonJsonRedisSerializer<>(objectMapper, CachedUserProfile.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, CachedCrewMeetingFeed> crewMeetingFeedRedisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, CachedCrewMeetingFeed> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(
        new JacksonJsonRedisSerializer<>(objectMapper, CachedCrewMeetingFeed.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, CachedMemberProfileRanking> memberProfileRankingRedisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, CachedMemberProfileRanking> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(
        new JacksonJsonRedisSerializer<>(objectMapper, CachedMemberProfileRanking.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, CachedMemberProfileCard> memberProfileCardRedisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, CachedMemberProfileCard> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(
        new JacksonJsonRedisSerializer<>(objectMapper, CachedMemberProfileCard.class));
    return template;
  }
}
