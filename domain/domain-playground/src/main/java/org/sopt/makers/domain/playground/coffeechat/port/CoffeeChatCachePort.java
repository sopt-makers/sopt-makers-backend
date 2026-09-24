package org.sopt.makers.domain.playground.coffeechat.port;

import java.util.Optional;

public interface CoffeeChatCachePort {

  Optional<String> getRandomCoffeeChatJson();

  void saveRandomCoffeeChatJson(String json);
}
