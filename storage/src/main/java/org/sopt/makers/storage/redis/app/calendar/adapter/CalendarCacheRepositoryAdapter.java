package org.sopt.makers.storage.redis.app.calendar.adapter;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.port.CalendarCacheRepositoryPort;
import org.sopt.makers.storage.redis.app.calendar.cache.CachedCalendars;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CalendarCacheRepositoryAdapter implements CalendarCacheRepositoryPort {

  private static final String KEY_PREFIX = "calendar:all:";
  private static final Duration TTL = Duration.ofDays(7);

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public Optional<List<Calendar>> findByGeneration(int generation) {
    return Optional.ofNullable(stringRedisTemplate.opsForValue().get(KEY_PREFIX + generation))
        .map(json -> objectMapper.readValue(json, CachedCalendars.class))
        .map(CachedCalendars::toDomain);
  }

  @Override
  public void save(int generation, List<Calendar> calendars) {
    String json = objectMapper.writeValueAsString(CachedCalendars.from(calendars));
    stringRedisTemplate.opsForValue().set(KEY_PREFIX + generation, json, TTL);
  }
}
