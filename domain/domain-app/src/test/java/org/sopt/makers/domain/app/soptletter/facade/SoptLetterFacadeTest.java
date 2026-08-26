package org.sopt.makers.domain.app.soptletter.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.DAILY_MESSAGE_LIMIT_EXCEEDED;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.FORBIDDEN_SOPT_LETTER;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.INVALID_PAGE_SIZE;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.NOT_FOUND_SOPT_LETTER;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterGenerator;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterProfileService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterTopicService;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterTopicRepositoryPort;

@DisplayName("SoptLetterFacade 테스트")
class SoptLetterFacadeTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 8, 24, 12, 0);
  private static final Long ME = 1L;
  private static final Long OTHER = 2L;
  private static final Long TOPIC_ID = 100L;

  private InMemorySoptLetterRepositoryPort letterPort;
  private InMemorySoptLetterLikeRepositoryPort likePort;
  private InMemorySoptLetterProfileRepositoryPort profilePort;
  private InMemorySoptLetterTopicRepositoryPort topicPort;
  private InMemoryOperationConfigPort operationConfigPort;
  private SoptLetterProfileService profileService;
  private SoptLetterFacade soptLetterFacade;

  @BeforeEach
  void setUp() {
    letterPort = new InMemorySoptLetterRepositoryPort();
    likePort = new InMemorySoptLetterLikeRepositoryPort();
    profilePort = new InMemorySoptLetterProfileRepositoryPort();
    topicPort = new InMemorySoptLetterTopicRepositoryPort();
    operationConfigPort = new InMemoryOperationConfigPort();

    Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    profileService =
        new SoptLetterProfileService(profilePort, count -> List.of("익명의 매운 떡볶이", "익명의 시원한 냉면"));
    SoptLetterTopicService topicService = new SoptLetterTopicService(topicPort, clock);
    SoptLetterService soptLetterService =
        new SoptLetterService(
            letterPort,
            likePort,
            profilePort,
            new SoptLetterGenerator(),
            new OperationConfigService(operationConfigPort),
            clock);
    soptLetterFacade = new SoptLetterFacade(soptLetterService, profileService, topicService);

    topicPort.add(new SoptLetterTopic(TOPIC_ID, "이번 주 주제", null, true, NOW, NOW, NOW));
    profileService.getOrCreate(ME);
    profileService.getOrCreate(OTHER);
  }

  @Test
  @DisplayName("메시지를 작성하면 작성자 닉네임과 내 글 표시가 붙는다")
  void createsMessage() {
    SoptLetterView view = soptLetterFacade.createMessage(ME, TOPIC_ID, "안녕하세요");

    assertThat(view.letter().message()).isEqualTo("안녕하세요");
    assertThat(view.authorNickname()).isEqualTo("익명의 매운 떡볶이");
    assertThat(view.mine()).isTrue();
    assertThat(view.likedByMe()).isFalse();
  }

  @Test
  @DisplayName("메시지 색은 주제 안에서 순서대로 돌아간다")
  void rotatesColor() {
    assertThat(soptLetterFacade.createMessage(ME, TOPIC_ID, "1").letter().color())
        .isEqualTo(SoptLetterColor.BLUE_50);
    assertThat(soptLetterFacade.createMessage(ME, TOPIC_ID, "2").letter().color())
        .isEqualTo(SoptLetterColor.GREEN_50);
    assertThat(soptLetterFacade.createMessage(ME, TOPIC_ID, "3").letter().color())
        .isEqualTo(SoptLetterColor.YELLOW_50);
  }

  @Test
  @DisplayName("하루 작성 한도를 넘기면 예외가 발생한다")
  void throwsWhenDailyLimitExceeded() {
    for (int i = 0; i < 10; i++) {
      soptLetterFacade.createMessage(ME, TOPIC_ID, "메시지 " + i);
    }

    assertThatThrownBy(() -> soptLetterFacade.createMessage(ME, TOPIC_ID, "11번째"))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(DAILY_MESSAGE_LIMIT_EXCEEDED);
  }

  @Test
  @DisplayName("어제 쓴 메시지는 오늘 한도에 포함되지 않는다")
  void yesterdayMessagesDoNotCount() {
    letterPort.setCreatedAt(NOW.minusDays(1));
    for (int i = 0; i < 10; i++) {
      soptLetterFacade.createMessage(ME, TOPIC_ID, "어제 " + i);
    }
    letterPort.setCreatedAt(NOW);

    assertThat(soptLetterFacade.createMessage(ME, TOPIC_ID, "오늘 첫 글")).isNotNull();
  }

  @Test
  @DisplayName("남의 메시지는 수정할 수 없다")
  void cannotUpdateOthersMessage() {
    Long letterId = soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글").letter().id();

    assertThatThrownBy(() -> soptLetterFacade.updateMessage(ME, TOPIC_ID, letterId, "고침"))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(FORBIDDEN_SOPT_LETTER);
  }

  @Test
  @DisplayName("다른 주제의 메시지는 없는 것으로 본다")
  void treatsMessageOfAnotherTopicAsMissing() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();

    assertThatThrownBy(() -> soptLetterFacade.updateMessage(ME, 999L, letterId, "고침"))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_SOPT_LETTER);
  }

  @Test
  @DisplayName("메시지를 지우면 달려 있던 좋아요도 함께 사라진다")
  void deletesLikesWithMessage() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();
    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);

    soptLetterFacade.deleteMessage(ME, TOPIC_ID, letterId);

    assertThat(likePort.existsByLetterIdAndUserId(letterId, OTHER)).isFalse();
  }

  @Test
  @DisplayName("같은 좋아요를 다시 눌러도 수가 늘지 않는다")
  void likeIsIdempotent() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();

    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);
    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);

    assertThat(letterPort.findById(letterId)).get().extracting("likeCount").isEqualTo(1);
  }

  @Test
  @DisplayName("누르지 않은 좋아요를 취소해도 수가 줄지 않는다")
  void unlikeWithoutLikeDoesNothing() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();
    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);

    soptLetterFacade.removeLike(ME, TOPIC_ID, letterId);

    assertThat(letterPort.findById(letterId)).get().extracting("likeCount").isEqualTo(1);
  }

  @Test
  @DisplayName("목록은 최신순이고 다음 커서와 다음 페이지 여부를 준다")
  void paginatesLatestFirst() {
    for (int i = 1; i <= 5; i++) {
      soptLetterFacade.createMessage(ME, TOPIC_ID, "메시지 " + i);
    }

    SoptLetterPage firstPage = soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 2);

    assertThat(firstPage.messages()).hasSize(2);
    assertThat(firstPage.hasNext()).isTrue();
    assertThat(firstPage.totalCount()).isEqualTo(5);
    assertThat(firstPage.messages().getFirst().letter().message()).isEqualTo("메시지 5");

    SoptLetterPage lastPage = soptLetterFacade.getTopicMessages(ME, TOPIC_ID, 2L, 2);

    assertThat(lastPage.hasNext()).isFalse();
    assertThat(lastPage.messages()).extracting(v -> v.letter().message()).containsExactly("메시지 1");
  }

  @Test
  @DisplayName("목록에 내 좋아요 여부와 내 글 여부가 붙는다")
  void marksLikedAndMine() {
    Long mine = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글");
    soptLetterFacade.addLike(ME, TOPIC_ID, mine);

    List<SoptLetterView> messages =
        soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 10).messages();

    assertThat(messages)
        .anySatisfy(
            view -> {
              assertThat(view.letter().message()).isEqualTo("내 글");
              assertThat(view.mine()).isTrue();
              assertThat(view.likedByMe()).isTrue();
            });
    assertThat(messages)
        .anySatisfy(
            view -> {
              assertThat(view.letter().message()).isEqualTo("남의 글");
              assertThat(view.mine()).isFalse();
              assertThat(view.authorNickname()).isEqualTo("익명의 시원한 냉면");
            });
  }

  @Test
  @DisplayName("조회 개수가 0 이하이면 예외가 발생한다")
  void throwsWhenPageSizeInvalid() {
    assertThatThrownBy(() -> soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 0))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(INVALID_PAGE_SIZE);
  }

  @Test
  @DisplayName("신고 폼 주소는 운영 설정에서 읽는다")
  void readsReportFormUrlFromOperationConfig() {
    operationConfigPort.save(
        OperationConfig.text(
            OperationConfigCategory.SOPT_LETTER, "linkUrl", "https://form.example", "신고 폼"));

    assertThat(soptLetterFacade.getReportFormUrl()).isEqualTo("https://form.example");
  }

  @Test
  @DisplayName("기본 주제 목록은 개별 주제 존재 여부를 함께 준다")
  void tellsWhetherNormalTopicExists() {
    assertThat(soptLetterFacade.getDefaultTopicMessages(ME, null, 10).hasNormalTopic()).isFalse();

    topicPort.add(new SoptLetterTopic(200L, "개별 주제", "CTA", false, NOW, NOW, NOW));

    assertThat(soptLetterFacade.getDefaultTopicMessages(ME, null, 10).hasNormalTopic()).isTrue();
  }
}
