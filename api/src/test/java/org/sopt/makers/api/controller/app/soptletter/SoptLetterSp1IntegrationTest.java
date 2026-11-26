package org.sopt.makers.api.controller.app.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.service.AppServiceBadgeService;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.auth.port.OAuthAuthenticatorPort;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(
    properties = "spring.datasource.url=jdbc:h2:mem:soptletter;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
@DisplayName("솝레터 SP1 통합 테스트")
class SoptLetterSp1IntegrationTest {

  private static final String DB_URL = System.getenv("SOPT_LETTER_TEST_DB_URL");
  private static final String BASE = "/api/v2/sopt-letter";
  private static final long ME = 9001L;
  private static final long OTHER = 9002L;
  private static final String ADMIN_BASE = "/api/v2/admin/notification/sopt-letter";
  private static final String API_KEY = "test-notification-api-key";
  private static final int CURRENT_GENERATION = 36;

  @MockitoBean OAuthAuthenticatorPort oAuthAuthenticatorPort;
  @MockitoBean TokenIssuerPort tokenIssuerPort;
  @MockitoBean SmsSenderPort smsSenderPort;
  @MockitoBean OkHttpClient gabiaOkHttpClient;
  @MockitoBean PushSenderPort pushSenderPort;

  @Autowired WebApplicationContext context;
  @Autowired JdbcTemplate jdbc;
  @Autowired AppServiceBadgeService appServiceBadgeService;
  @Autowired OperationConfigService operationConfigService;
  @Autowired EntityManager em;
  @Autowired PlatformTransactionManager transactionManager;

  private MockMvc mvc;
  private LocalDateTime now;
  private long otherProfileId;
  private final List<Long> createdUserIds = new ArrayList<>();

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry properties) {
    if (DB_URL != null) {
      properties.add("spring.datasource.url", () -> DB_URL);
      properties.add("spring.datasource.username", () -> System.getenv("SOPT_LETTER_TEST_DB_USER"));
      properties.add(
          "spring.datasource.password", () -> System.getenv("SOPT_LETTER_TEST_DB_PASSWORD"));
      properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
      properties.add("spring.datasource.hikari.schema", () -> "sopt_letter_test");
      properties.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
      properties.add("spring.flyway.enabled", () -> "true");
      properties.add("spring.flyway.schemas", () -> "sopt_letter_test");
      properties.add("spring.flyway.default-schema", () -> "sopt_letter_test");
    }
  }

  @BeforeEach
  void setUp() {
    cleanUp();
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
    SecurityContextHolder.getContext()
        .setAuthentication(new CustomAuthentication(String.valueOf(ME), "", List.of()));
    now = LocalDateTime.now();
    profile(ME, "익명의 매운 떡볶이", false);
    otherProfileId = profile(OTHER, "익명의 시원한 냉면", false);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
    cleanUp();
  }

  @Test
  @DisplayName("주제 목록은 기본 주제를 먼저 주고, 기간 밖 주제를 빼고, SP1 필드를 내려준다")
  void topicsResponse() throws Exception {
    long defaultTopic = topic("기본 주제", "부제", true, now.minusDays(1), now.plusDays(1), now);
    long normalTopic =
        topic("개별 주제", null, false, now.minusDays(1), now.plusDays(1), now.minusDays(8));
    topic("만료 주제", null, false, now.minusDays(10), now.minusDays(1), now.minusDays(10));
    letter(otherProfileId, defaultTopic, now.minusHours(1));
    letter(otherProfileId, defaultTopic, now.minusHours(1));

    mvc.perform(get(BASE + "/topics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.topics.length()").value(2))
        .andExpect(jsonPath("$.data.topics[0].topicId").value(defaultTopic))
        .andExpect(jsonPath("$.data.topics[0].subtitle").value("부제"))
        .andExpect(jsonPath("$.data.topics[0].isDefault").value(true))
        .andExpect(jsonPath("$.data.topics[0].isNew").value(true))
        .andExpect(jsonPath("$.data.topics[0].isHot").value(true))
        .andExpect(jsonPath("$.data.topics[0].unreadCount").value(2))
        .andExpect(jsonPath("$.data.topics[1].topicId").value(normalTopic))
        .andExpect(jsonPath("$.data.topics[1].isNew").value(false))
        .andExpect(jsonPath("$.data.topics[1].isHot").value(false))
        .andExpect(jsonPath("$.data.topics[1].unreadCount").value(0));
  }

  @Test
  @DisplayName("기간 밖 주제에 메시지를 쓰면 400을 돌려준다")
  void rejectsWritingOutsidePeriod() throws Exception {
    long expired =
        topic("만료 주제", null, false, now.minusDays(10), now.minusDays(1), now.minusDays(10));

    mvc.perform(
            post(BASE + "/topics/" + expired + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"늦은 글\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("홈 솝레터 뱃지는 실제 빈 연결로 안 읽은 수 합계를 보여 준다")
  void homeBadge() throws Exception {
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    letter(otherProfileId, topic, now.minusHours(1));
    letter(otherProfileId, topic, now.minusHours(1));
    letter(otherProfileId, topic, now.minusHours(1));

    AppServiceEntryStatus status =
        appServiceBadgeService
            .getAppServiceEntryStatusResponseAsync(
                new AppService(1L, "SOPT_LETTER", true, false, null, null, now), ME)
            .get();

    assertThat(status.displayAlarmBadge()).isTrue();
    assertThat(status.alarmBadge()).isEqualTo("3");
  }

  @Test
  @DisplayName("첫 페이지를 조회하면 비동기 리스너가 읽음 기록을 남기고 안 읽은 수가 0이 된다")
  void firstPageMarksTopicAsRead() throws Exception {
    assumeTrue(DB_URL != null, "읽음 기록 upsert는 PostgreSQL 문법이라 로컬 PostgreSQL에서만 돈다");
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    letter(otherProfileId, topic, now.minusHours(2));
    long latest = letter(otherProfileId, topic, now.minusHours(1));

    mvc.perform(get(BASE + "/topics/" + topic + "/messages").param("size", "1"))
        .andExpect(status().isOk());

    assertThat(waitForLastRead(topic)).isEqualTo(latest);
    mvc.perform(get(BASE + "/topics")).andExpect(jsonPath("$.data.topics[0].unreadCount").value(0));
  }

  @Test
  @DisplayName("Flyway가 V4를 적용하고 성공으로 기록한다")
  void flywayAppliesV4() {
    assumeTrue(DB_URL != null, "Flyway는 로컬 PostgreSQL을 지정했을 때만 켠다");

    Boolean success =
        jdbc.queryForObject(
            "SELECT success FROM sopt_letter_test.flyway_schema_history WHERE version = '4'",
            Boolean.class);

    assertThat(success).isTrue();
  }

  @Test
  @DisplayName("어드민 알림 API는 API 키가 틀리면 401을 돌려주고 보내지 않는다")
  void adminNotificationRejectsWrongApiKey() throws Exception {
    mvc.perform(
            post(ADMIN_BASE + "/unwritten-reminder")
                .header("apiKey", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isUnauthorized());

    verify(pushSenderPort, never()).send(any());
  }

  @Test
  @DisplayName("미작성 리마인드는 현재 기수 회원 중 솝레터를 쓰지 않은 사람에게만 보낸다")
  void unwrittenReminderSendsToUnwrittenCurrentMembers() throws Exception {
    long unwritten = user(CURRENT_GENERATION);
    long written = user(CURRENT_GENERATION);
    user(CURRENT_GENERATION - 1);
    profile(written, "익명의 달콤한 호떡", true);

    mvc.perform(
            post(ADMIN_BASE + "/unwritten-reminder")
                .header("apiKey", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isOk());

    assertThat(sentPush().userIds()).containsExactly(unwritten);
  }

  @Test
  @DisplayName("새 주제 알림은 현재 기수 회원 전체에게 보낸다")
  void newTopicSendsToAllCurrentMembers() throws Exception {
    long first = user(CURRENT_GENERATION);
    long second = user(CURRENT_GENERATION);
    user(CURRENT_GENERATION - 1);

    mvc.perform(
            post(ADMIN_BASE + "/new-topic")
                .header("apiKey", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isOk());

    PushMessage push = sentPush();
    assertThat(push.userIds()).containsExactlyInAnyOrder(first, second);
    assertThat(push.title()).isEqualTo("새 주제가 열렸어요");
    assertThat(push.deepLink()).isEqualTo("soptletter");
  }

  @Test
  @DisplayName("좋아요를 누르면 커밋 뒤 현재 기수 활동 회원인 글쓴이에게 알림을 보낸다")
  void likeSendsAlarmToActiveAuthor() throws Exception {
    assumeTrue(DB_URL != null, "좋아요 저장은 PostgreSQL 문법이라 로컬 PostgreSQL에서만 돈다");
    operationConfigService.upsertValue(
        OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle", "좋아요가 달렸어요", "제목");
    operationConfigService.upsertValue(
        OperationConfigCategory.SOPT_LETTER, "likeNotificationContent", "확인해 보세요", "내용");
    long author = user(CURRENT_GENERATION);
    long authorProfileId = profile(author, "익명의 고소한 붕어빵", false);
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    long letterId = letter(authorProfileId, topic, now.minusHours(1));

    mvc.perform(post(BASE + "/topics/" + topic + "/messages/" + letterId + "/likes"))
        .andExpect(status().isOk());

    ArgumentCaptor<PushMessage> captor = ArgumentCaptor.forClass(PushMessage.class);
    verify(pushSenderPort, timeout(5000)).send(captor.capture());
    PushMessage push = captor.getValue();
    assertThat(push.userIds()).containsExactly(author);
    assertThat(push.title()).isEqualTo("좋아요가 달렸어요");
    assertThat(push.deepLink())
        .isEqualTo(
            "soptletter?topicId="
                + topic
                + "&messageId="
                + letterId
                + "&pushType=soptletter_liked");
  }

  @Test
  @DisplayName("내 글에 좋아요를 누르면 403을 돌려준다")
  void cannotLikeOwnMessage() throws Exception {
    long myProfileId =
        jdbc.queryForObject("SELECT id FROM sopt_letter_profile WHERE user_id = ?", Long.class, ME);
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    long letterId = letter(myProfileId, topic, now.minusHours(1));

    mvc.perform(post(BASE + "/topics/" + topic + "/messages/" + letterId + "/likes"))
        .andExpect(status().isForbidden());
  }

  private PushMessage sentPush() {
    ArgumentCaptor<PushMessage> captor = ArgumentCaptor.forClass(PushMessage.class);
    verify(pushSenderPort).send(captor.capture());
    return captor.getValue();
  }

  private String notificationBody() {
    return "{\"notificationTitle\":\"새 주제가 열렸어요\","
        + "\"notificationContent\":\"앱잼 회고를 남겨 보세요\","
        + "\"deepLink\":\"soptletter\"}";
  }

  private long user(int generation) {
    Long userId =
        new TransactionTemplate(transactionManager)
            .execute(
                status -> {
                  UserEntity user =
                      UserEntity.create(
                          "김솝트",
                          "0100000" + (1000 + createdUserIds.size()),
                          null,
                          null,
                          null,
                          "test-" + System.nanoTime(),
                          OAuthPlatform.GOOGLE);
                  em.persist(user);
                  em.persist(
                      UserActivityHistoryEntity.create(
                          user, generation, null, Part.SERVER, Role.MEMBER, true, null));
                  return user.getId();
                });
    createdUserIds.add(userId);
    return userId;
  }

  private Long waitForLastRead(long topicId) throws InterruptedException {
    for (int i = 0; i < 50; i++) {
      List<Long> found =
          jdbc.queryForList(
              "SELECT last_read_letter_id FROM sopt_letter_topic_read"
                  + " WHERE user_id = ? AND topic_id = ?",
              Long.class,
              ME,
              topicId);
      if (!found.isEmpty()) {
        return found.getFirst();
      }
      Thread.sleep(100);
    }
    return null;
  }

  private long profile(long userId, String nickname, boolean hasWritten) {
    jdbc.update(
        "INSERT INTO sopt_letter_profile"
            + " (user_id, nickname, is_onboarded, has_written, created_at, updated_at)"
            + " VALUES (?, ?, true, ?, ?, ?)",
        userId,
        nickname,
        hasWritten,
        now,
        now);
    return jdbc.queryForObject(
        "SELECT id FROM sopt_letter_profile WHERE user_id = ?", Long.class, userId);
  }

  private long topic(
      String title,
      String subtitle,
      boolean isDefault,
      LocalDateTime startedAt,
      LocalDateTime endedAt,
      LocalDateTime createdAt) {
    jdbc.update(
        "INSERT INTO sopt_letter_topic"
            + " (title, subtitle, is_default, started_at, ended_at, created_at, updated_at)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?)",
        title,
        subtitle,
        isDefault,
        startedAt,
        endedAt,
        createdAt,
        createdAt);
    return jdbc.queryForObject(
        "SELECT max(id) FROM sopt_letter_topic WHERE title = ?", Long.class, title);
  }

  private long letter(long authorProfileId, long topicId, LocalDateTime createdAt) {
    jdbc.update(
        "INSERT INTO sopt_letter"
            + " (author_profile_id, topic_id, degree, message, like_count, created_at, updated_at)"
            + " VALUES (?, ?, 0, '글', 0, ?, ?)",
        authorProfileId,
        topicId,
        createdAt,
        createdAt);
    return jdbc.queryForObject("SELECT max(id) FROM sopt_letter", Long.class);
  }

  private void cleanUp() {
    for (Long userId : createdUserIds) {
      jdbc.update("DELETE FROM user_activity_histories WHERE user_id = ?", userId);
      jdbc.update("DELETE FROM users WHERE id = ?", userId);
    }
    createdUserIds.clear();
    jdbc.update(
        "DELETE FROM operation_configs WHERE category = 'SOPT_LETTER'"
            + " AND config_key IN ('likeNotificationTitle', 'likeNotificationContent')");
    jdbc.update("DELETE FROM sopt_letter_like_notification");
    jdbc.update("DELETE FROM sopt_letter_like");
    jdbc.update("DELETE FROM sopt_letter_topic_read");
    jdbc.update("DELETE FROM sopt_letter");
    jdbc.update("DELETE FROM sopt_letter_topic");
    jdbc.update("DELETE FROM sopt_letter_profile");
  }
}
