package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatException;
import org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure;
import org.sopt.makers.domain.playground.coffeechat.port.AnonymousProfileImagePort;
import org.sopt.makers.storage.db.playground.coffeechat.entity.AnonymousProfileImageEntity;
import org.sopt.makers.storage.db.playground.coffeechat.repository.AnonymousProfileImageJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnonymousProfileImageAdapter implements AnonymousProfileImagePort {

  private static final List<Long> ANONYMOUS_IMAGE_IDS = List.of(1L, 2L, 3L, 4L, 5L);

  private final AnonymousProfileImageJpaRepository anonymousProfileImageJpaRepository;

  @Override
  public AnonymousImage getRandomImage() {
    Long randomId =
        ANONYMOUS_IMAGE_IDS.get(ThreadLocalRandom.current().nextInt(ANONYMOUS_IMAGE_IDS.size()));
    AnonymousProfileImageEntity entity =
        anonymousProfileImageJpaRepository
            .findById(randomId)
            .orElseThrow(
                () -> new CoffeeChatException(CoffeeChatFailure.ANONYMOUS_PROFILE_IMAGE_NOT_FOUND));
    return new AnonymousImage(entity.getId(), entity.getImageUrl());
  }
}
