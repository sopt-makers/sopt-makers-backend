package org.sopt.makers.storage.config;

import org.sopt.makers.storage.redis.playground.cache.CachedCrewMeetingFeed;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileCard;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileRanking;
import org.sopt.makers.storage.redis.user.cache.CachedUserProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

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
