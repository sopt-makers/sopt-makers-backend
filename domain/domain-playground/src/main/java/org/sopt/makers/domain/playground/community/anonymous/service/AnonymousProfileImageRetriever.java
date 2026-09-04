package org.sopt.makers.domain.playground.community.anonymous.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousProfileImageRepositoryPort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnonymousProfileImageRetriever {

  private static final Long MAKERS_LOGO_IMAGE_ID = 6L;

  private final AnonymousProfileImageRepositoryPort anonymousProfileImageRepositoryPort;

  private final Map<Long, AnonymousProfileImage> profileImageMap = new ConcurrentHashMap<>();
  private volatile boolean initialized = false;

  private void ensureInitialized() {
    if (!initialized) {
      synchronized (this) {
        if (!initialized) {
          try {
            List<AnonymousProfileImage> anonymousProfileImages =
                anonymousProfileImageRepositoryPort.findAllByIdNot(MAKERS_LOGO_IMAGE_ID);
            for (AnonymousProfileImage image : anonymousProfileImages) {
              profileImageMap.put(image.id(), image);
            }
            initialized = true;
          } catch (Exception e) {
            log.warn("익명 프로필 이미지 초기화 실패, 다음 요청 시 재시도합니다.", e);
          }
        }
      }
    }
  }

  public AnonymousProfileImage getAnonymousProfileImage() {
    ensureInitialized();
    long randomImageNumber = ThreadLocalRandom.current().nextLong(1, 6);
    return profileImageMap.get(randomImageNumber);
  }
}
