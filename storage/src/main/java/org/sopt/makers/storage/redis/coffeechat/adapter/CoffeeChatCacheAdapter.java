package org.sopt.makers.storage.redis.coffeechat.adapter;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoffeeChatCacheAdapter implements CoffeeChatCachePort {

  private static final String KEY = "coffeeChat:random";
  private static final Duration TTL = Duration.ofHours(25);

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public Optional<String> getRandomCoffeeChatJson() {
    return Optional.ofNullable(stringRedisTemplate.opsForValue().get(KEY));
  }

  @Override
  public void saveRandomCoffeeChatJson(String json) {
    stringRedisTemplate.opsForValue().set(KEY, json, TTL);
  }
}
