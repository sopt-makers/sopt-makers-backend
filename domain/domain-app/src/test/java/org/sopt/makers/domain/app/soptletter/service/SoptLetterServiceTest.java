package org.sopt.makers.domain.app.soptletter.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterRepositoryPort;

@DisplayName("SoptLetterService 테스트 (Facade 없이 Port만으로)")
class SoptLetterServiceTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 8, 24, 12, 0);
  private static final SoptLetterTopic TOPIC =
      new SoptLetterTopic(100L, "이번 주 주제", null, true, NOW, NOW, NOW);
  private static final SoptLetterProfile ME = new SoptLetterProfile(1L, 10L, "차은우", true);

  private SoptLetterService soptLetterService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    soptLetterService =
        new SoptLetterService(
            new InMemorySoptLetterRepositoryPort(),
            new InMemorySoptLetterLikeRepositoryPort(),
            new InMemorySoptLetterProfileRepositoryPort(),
            new SoptLetterGenerator(),
            new OperationConfigService(new InMemoryOperationConfigPort()),
            clock);
  }

  @Test
  @DisplayName("작성자 프로필과 주제를 그대로 넘기면 메시지가 만들어진다")
  void createsMessageFromResolvedProfileAndTopic() {
    SoptLetterView view = soptLetterService.create(ME, TOPIC, "안녕하세요");

    assertThat(view.letter().message()).isEqualTo("안녕하세요");
    assertThat(view.letter().topicId()).isEqualTo(TOPIC.id());
    assertThat(view.authorNickname()).isEqualTo("차은우");
    assertThat(view.mine()).isTrue();
  }
}
