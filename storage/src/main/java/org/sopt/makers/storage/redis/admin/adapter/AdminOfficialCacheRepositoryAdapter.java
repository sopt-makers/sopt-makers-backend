package org.sopt.makers.storage.redis.admin.adapter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AdminOfficialCacheRepositoryAdapter implements AdminCachePort {

  private static final String COMMON_KEY_PREFIX = "admin:official:common:";
  private static final String HOME_KEY_PREFIX = "admin:official:home:";
  private static final String ABOUT_KEY_PREFIX = "admin:official:about:";
  private static final String RECRUIT_KEY_PREFIX = "admin:official:recruit:";
  private static final Duration TTL = Duration.ofHours(1);

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void putCommon(Integer generationId, CommonCacheData data) {
    stringRedisTemplate.opsForValue().set(COMMON_KEY_PREFIX + generationId, serialize(data), TTL);
  }

  @Override
  public CommonCacheData getCommon(Integer generationId) {
    String json = stringRedisTemplate.opsForValue().get(COMMON_KEY_PREFIX + generationId);
    return json != null ? deserialize(json, CommonCacheData.class) : null;
  }

  @Override
  public void evictCommon(Integer generationId) {
    stringRedisTemplate.delete(COMMON_KEY_PREFIX + generationId);
  }

  @Override
  public void putHome(Integer generationId, HomeCacheData data) {
    stringRedisTemplate.opsForValue().set(HOME_KEY_PREFIX + generationId, serialize(data), TTL);
  }

  @Override
  public HomeCacheData getHome(Integer generationId) {
    String json = stringRedisTemplate.opsForValue().get(HOME_KEY_PREFIX + generationId);
    return json != null ? deserialize(json, HomeCacheData.class) : null;
  }

  @Override
  public void evictHome(Integer generationId) {
    stringRedisTemplate.delete(HOME_KEY_PREFIX + generationId);
  }

  @Override
  public void putAbout(Integer generationId, AboutCacheData data) {
    stringRedisTemplate.opsForValue().set(ABOUT_KEY_PREFIX + generationId, serialize(data), TTL);
  }

  @Override
  public AboutCacheData getAbout(Integer generationId) {
    String json = stringRedisTemplate.opsForValue().get(ABOUT_KEY_PREFIX + generationId);
    return json != null ? deserialize(json, AboutCacheData.class) : null;
  }

  @Override
  public void evictAbout(Integer generationId) {
    stringRedisTemplate.delete(ABOUT_KEY_PREFIX + generationId);
  }

  @Override
  public void putRecruit(Integer generationId, RecruitCacheData data) {
    stringRedisTemplate.opsForValue().set(RECRUIT_KEY_PREFIX + generationId, serialize(data), TTL);
  }

  @Override
  public RecruitCacheData getRecruit(Integer generationId) {
    String json = stringRedisTemplate.opsForValue().get(RECRUIT_KEY_PREFIX + generationId);
    return json != null ? deserialize(json, RecruitCacheData.class) : null;
  }

  @Override
  public void evictRecruit(Integer generationId) {
    stringRedisTemplate.delete(RECRUIT_KEY_PREFIX + generationId);
  }

  private String serialize(Object data) {
    return objectMapper.writeValueAsString(data);
  }

  private <T> T deserialize(String json, Class<T> type) {
    return objectMapper.readValue(json, type);
  }
}
