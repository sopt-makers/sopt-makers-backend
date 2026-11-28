package org.sopt.makers.domain.app.soptletter.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.DAILY_MESSAGE_LIMIT_EXCEEDED;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.FORBIDDEN_SELF_LIKE;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.FORBIDDEN_SOPT_LETTER;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.INACTIVE_SOPT_LETTER_TOPIC;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.INVALID_PAGE_SIZE;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.NOT_FOUND_SOPT_LETTER;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterLikeEvent;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicReadEvent;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicView;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterGenerator;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterProfileService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterTopicReadEventListener;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterTopicService;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeNotificationRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterLikeRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterTopicReadRepositoryPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterTopicRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;

@DisplayName("SoptLetterFacade 테스트")
class SoptLetterFacadeTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 8, 24, 12, 0);
  private static final Long ME = 1L;
  private static final Long OTHER = 2L;
  private static final Long TOPIC_ID = 100L;

  private InMemorySoptLetterTopicReadRepositoryPort readPort;
  private InMemorySoptLetterRepositoryPort letterPort;
  private InMemorySoptLetterLikeRepositoryPort likePort;
  private InMemorySoptLetterProfileRepositoryPort profilePort;
  private InMemorySoptLetterTopicRepositoryPort topicPort;
  private InMemoryOperationConfigPort operationConfigPort;
  private SoptLetterProfileService profileService;
  private SoptLetterFacade soptLetterFacade;
  private List<Object> publishedEvents;

  @BeforeEach
  void setUp() {
    readPort = new InMemorySoptLetterTopicReadRepositoryPort();
    topicPort = new InMemorySoptLetterTopicRepositoryPort();
    letterPort = new InMemorySoptLetterRepositoryPort(readPort, topicPort);
    likePort = new InMemorySoptLetterLikeRepositoryPort();
    profilePort = new InMemorySoptLetterProfileRepositoryPort();
    operationConfigPort = new InMemoryOperationConfigPort();

    Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    profileService =
        new SoptLetterProfileService(profilePort, count -> List.of("익명의 매운 떡볶이", "익명의 시원한 냉면"));
    SoptLetterTopicService topicService = new SoptLetterTopicService(topicPort, clock);
    SoptLetterTopicReadEventListener readEventListener =
        new SoptLetterTopicReadEventListener(readPort);
    publishedEvents = new ArrayList<>();
    ApplicationEventPublisher eventPublisher =
        event -> {
          publishedEvents.add(event);
          if (event instanceof SoptLetterTopicReadEvent readEvent) {
            readEventListener.handle(readEvent);
          }
        };
    SoptLetterService soptLetterService =
        new SoptLetterService(
            letterPort,
            likePort,
            new InMemorySoptLetterLikeNotificationRepositoryPort(),
            profilePort,
            new SoptLetterGenerator(),
            new OperationConfigService(operationConfigPort),
            clock,
            eventPublisher);
    soptLetterFacade =
        new SoptLetterFacade(soptLetterService, profileService, topicService, eventPublisher);

    topicPort.add(new SoptLetterTopic(TOPIC_ID, "이번 주 주제", null, null, true, NOW, NOW, NOW));
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
    soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글");
    Long others = soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글").letter().id();
    soptLetterFacade.addLike(ME, TOPIC_ID, others);

    List<SoptLetterView> messages =
        soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 10).messages();

    assertThat(messages)
        .anySatisfy(
            view -> {
              assertThat(view.letter().message()).isEqualTo("내 글");
              assertThat(view.mine()).isTrue();
              assertThat(view.likedByMe()).isFalse();
            });
    assertThat(messages)
        .anySatisfy(
            view -> {
              assertThat(view.letter().message()).isEqualTo("남의 글");
              assertThat(view.mine()).isFalse();
              assertThat(view.likedByMe()).isTrue();
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

    topicPort.add(new SoptLetterTopic(200L, "개별 주제", null, "CTA", false, NOW, NOW, NOW));

    assertThat(soptLetterFacade.getDefaultTopicMessages(ME, null, 10).hasNormalTopic()).isTrue();
  }

  @Test
  @DisplayName("CTA는 기본 주제도 대상에 포함한다")
  void findsActiveCtaIncludingDefaultTopic() {
    topicPort.add(
        new SoptLetterTopic(
            201L, "기본 주제", null, "CTA-default", true, NOW.minusDays(1), NOW.plusDays(1), NOW));

    assertThat(soptLetterFacade.findActiveCta()).get().extracting("id").isEqualTo(201L);
  }

  @Test
  @DisplayName("CTA가 여러 개면 시작일이 가장 늦은 주제를 우선한다")
  void findsActiveCtaByLatestStartedAt() {
    topicPort.add(
        new SoptLetterTopic(
            201L, "먼저 시작한 주제", null, "CTA-earlier", false, NOW.minusDays(1), NOW.plusDays(1), NOW));
    topicPort.add(
        new SoptLetterTopic(
            202L, "더 늦게 시작한 주제", null, "CTA-later", false, NOW, NOW.plusDays(1), NOW));

    assertThat(soptLetterFacade.findActiveCta()).get().extracting("id").isEqualTo(202L);
  }

  @Test
  @DisplayName("기간 밖의 주제에는 메시지를 쓸 수 없다")
  void cannotWriteOutsideTopicPeriod() {
    topicPort.add(topic(300L, false, NOW.minusDays(10), NOW.minusDays(1), NOW.minusDays(10)));

    assertThatThrownBy(() -> soptLetterFacade.createMessage(ME, 300L, "늦은 글"))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(INACTIVE_SOPT_LETTER_TOPIC);
  }

  @Test
  @DisplayName("전체 주제 목록은 기본 주제를 먼저, 그다음 최근에 만든 순으로 준다")
  void listsDefaultTopicFirstThenLatest() {
    topicPort.add(topic(301L, false, NOW.minusDays(1), NOW.plusDays(1), NOW.minusHours(1)));
    topicPort.add(topic(302L, false, NOW.minusDays(1), NOW.plusDays(1), NOW.minusHours(2)));

    assertThat(soptLetterFacade.getTopics(ME, null))
        .extracting(view -> view.topic().id())
        .containsExactly(TOPIC_ID, 301L, 302L);
  }

  @Test
  @DisplayName("주제 목록은 종류와 상관없이 기간 안의 주제만 준다")
  void listsOnlyActiveTopics() {
    topicPort.add(topic(303L, false, NOW.minusDays(10), NOW.minusDays(1), NOW.minusDays(10)));
    topicPort.add(topic(304L, true, NOW.plusDays(1), NOW.plusDays(10), NOW));

    assertThat(soptLetterFacade.getTopics(ME, null))
        .extracting(view -> view.topic().id())
        .containsExactly(TOPIC_ID);
    assertThat(soptLetterFacade.getTopics(ME, "default"))
        .extracting(view -> view.topic().id())
        .containsExactly(TOPIC_ID);
    assertThat(soptLetterFacade.getTopics(ME, "normal")).isEmpty();
  }

  @Test
  @DisplayName("기본 주제 메시지 조회는 기간이 지난 기본 주제도 찾는다")
  void defaultTopicMessagesIgnorePeriod() {
    topicPort.add(topic(305L, true, NOW.minusDays(10), NOW.minusDays(1), NOW.plusHours(1)));

    assertThat(soptLetterFacade.getDefaultTopicMessages(ME, null, 10).topic().id()).isEqualTo(305L);
  }

  @Test
  @DisplayName("최근 7일 동안 글이 많은 주제 2개가 HOT이다")
  void marksTopTwoTopicsByRecentLettersAsHot() {
    topicPort.add(topic(306L, false, NOW.minusDays(1), NOW.plusDays(1), NOW.minusDays(1)));
    topicPort.add(topic(307L, false, NOW.minusDays(1), NOW.plusDays(1), NOW.minusDays(2)));
    letterPort.setCreatedAt(NOW.minusDays(8));
    for (int i = 0; i < 5; i++) {
      soptLetterFacade.createMessage(OTHER, TOPIC_ID, "오래된 글 " + i);
    }
    letterPort.setCreatedAt(NOW.minusDays(1));
    soptLetterFacade.createMessage(OTHER, 306L, "최근 글 1");
    soptLetterFacade.createMessage(OTHER, 306L, "최근 글 2");
    soptLetterFacade.createMessage(OTHER, 307L, "최근 글 3");

    assertThat(soptLetterFacade.getTopics(ME, null))
        .filteredOn(SoptLetterTopicView::isHot)
        .extracting(view -> view.topic().id())
        .containsExactlyInAnyOrder(306L, 307L);
  }

  @Test
  @DisplayName("만든 지 7일이 안 된 주제는 NEW이다")
  void marksRecentlyCreatedTopicAsNew() {
    topicPort.add(topic(308L, false, NOW.minusDays(10), NOW.plusDays(1), NOW.minusDays(8)));

    assertThat(soptLetterFacade.getTopics(ME, "normal"))
        .extracting(SoptLetterTopicView::isNew)
        .containsExactly(false);
    assertThat(soptLetterFacade.getTopics(ME, "default"))
        .extracting(SoptLetterTopicView::isNew)
        .containsExactly(true);
  }

  @Test
  @DisplayName("읽음 기록이 없으면 남이 쓴 글 전부가 안 읽은 글이다")
  void countsAllOthersLettersWhenNeverRead() {
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글 1");
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글 2");
    soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글");

    assertThat(unreadCountOf(ME, TOPIC_ID)).isEqualTo(2);
  }

  @Test
  @DisplayName("첫 페이지를 조회하면 그 주제를 다 읽은 것으로 본다")
  void firstPageMarksTopicAsRead() {
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글 1");
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글 2");

    soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 1);

    assertThat(unreadCountOf(ME, TOPIC_ID)).isZero();
  }

  @Test
  @DisplayName("읽은 뒤에 올라온 남의 글만 안 읽은 글로 센다")
  void countsOnlyLettersAfterLastRead() {
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "읽은 글");
    soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 10);
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "새 글");
    soptLetterFacade.createMessage(ME, TOPIC_ID, "내 새 글");

    assertThat(unreadCountOf(ME, TOPIC_ID)).isEqualTo(1);
  }

  @Test
  @DisplayName("다른 주제의 읽음 기록은 섞이지 않는다")
  void readRecordIsPerTopic() {
    topicPort.add(topic(309L, false, NOW.minusDays(1), NOW.plusDays(1), NOW.minusDays(1)));
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "기본 주제 글");
    soptLetterFacade.createMessage(OTHER, 309L, "개별 주제 글");

    soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 10);

    assertThat(unreadCountOf(ME, TOPIC_ID)).isZero();
    assertThat(unreadCountOf(ME, 309L)).isEqualTo(1);
  }

  @Test
  @DisplayName("기본 주제 메시지 조회도 첫 페이지면 읽음으로 기록한다")
  void defaultTopicFirstPageMarksRead() {
    soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글");

    soptLetterFacade.getDefaultTopicMessages(ME, null, 10);

    assertThat(unreadCountOf(ME, TOPIC_ID)).isZero();
  }

  @Test
  @DisplayName("커서가 있는 조회와 글이 없는 주제 조회는 읽음 이벤트를 내지 않는다")
  void doesNotPublishReadEventForCursorOrEmptyPage() {
    soptLetterFacade.getTopicMessages(ME, TOPIC_ID, null, 10);
    Long letterId = soptLetterFacade.createMessage(OTHER, TOPIC_ID, "남의 글").letter().id();
    soptLetterFacade.getTopicMessages(ME, TOPIC_ID, letterId + 1, 10);

    assertThat(publishedEvents).noneMatch(SoptLetterTopicReadEvent.class::isInstance);
  }

  @Test
  @DisplayName("읽음 기록은 더 큰 메시지 id로만 바뀐다")
  void readRecordOnlyMovesForward() {
    readPort.upsertLastReadLetterId(ME, TOPIC_ID, 10L);
    readPort.upsertLastReadLetterId(ME, TOPIC_ID, 3L);

    assertThat(readPort.lastReadLetterId(ME, TOPIC_ID)).isEqualTo(10L);
  }

  @Test
  @DisplayName("내 글에는 좋아요를 누를 수 없다")
  void cannotLikeOwnMessage() {
    Long mine = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();

    assertThatThrownBy(() -> soptLetterFacade.addLike(ME, TOPIC_ID, mine))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(FORBIDDEN_SELF_LIKE);
    assertThat(letterPort.findById(mine)).get().extracting("likeCount").isEqualTo(0);
  }

  @Test
  @DisplayName("처음 누른 좋아요는 글쓴이에게 보낼 좋아요 이벤트를 발행한다")
  void firstLikePublishesLikeEvent() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();

    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);

    assertThat(publishedEvents)
        .filteredOn(SoptLetterLikeEvent.class::isInstance)
        .containsExactly(new SoptLetterLikeEvent(OTHER, ME, TOPIC_ID, letterId));
  }

  @Test
  @DisplayName("좋아요를 취소했다가 다시 눌러도 좋아요 이벤트를 다시 발행하지 않는다")
  void relikeDoesNotPublishAgain() {
    Long letterId = soptLetterFacade.createMessage(ME, TOPIC_ID, "내 글").letter().id();

    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);
    soptLetterFacade.removeLike(OTHER, TOPIC_ID, letterId);
    soptLetterFacade.addLike(OTHER, TOPIC_ID, letterId);

    assertThat(publishedEvents).filteredOn(SoptLetterLikeEvent.class::isInstance).hasSize(1);
    assertThat(letterPort.findById(letterId)).get().extracting("likeCount").isEqualTo(1);
  }

  @Test
  @DisplayName("메시지를 쓰면 프로필에 작성 여부가 남는다")
  void marksProfileAsWritten() {
    assertThat(profilePort.findByUserId(ME)).get().extracting("hasWritten").isEqualTo(false);

    soptLetterFacade.createMessage(ME, TOPIC_ID, "첫 글");

    assertThat(profilePort.findByUserId(ME)).get().extracting("hasWritten").isEqualTo(true);
    assertThat(profilePort.findByUserId(OTHER)).get().extracting("hasWritten").isEqualTo(false);
  }

  private long unreadCountOf(Long userId, Long topicId) {
    return soptLetterFacade.getTopics(userId, null).stream()
        .filter(view -> view.topic().id().equals(topicId))
        .findFirst()
        .orElseThrow()
        .unreadCount();
  }

  private SoptLetterTopic topic(
      Long id,
      boolean isDefault,
      LocalDateTime startedAt,
      LocalDateTime endedAt,
      LocalDateTime createdAt) {
    return new SoptLetterTopic(
        id, "주제 " + id, null, null, isDefault, startedAt, endedAt, createdAt);
  }
}
