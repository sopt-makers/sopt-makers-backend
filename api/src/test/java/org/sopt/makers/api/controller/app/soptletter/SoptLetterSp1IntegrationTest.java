package org.sopt.makers.api.controller.app.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.service.AppServiceBadgeService;
import org.sopt.makers.domain.auth.port.OAuthAuthenticatorPort;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
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
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(
    properties = "spring.datasource.url=jdbc:h2:mem:soptletter;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
@DisplayName("솝레터 SP1 통합 테스트")
class SoptLetterSp1IntegrationTest {

  private static final String DB_URL = System.getenv("SOPT_LETTER_TEST_DB_URL");
  private static final String BASE = "/api/v2/sopt-letter";
  private static final long ME = 1L;
  private static final long OTHER = 2L;

  @MockitoBean OAuthAuthenticatorPort oAuthAuthenticatorPort;
  @MockitoBean TokenIssuerPort tokenIssuerPort;
  @MockitoBean SmsSenderPort smsSenderPort;
  @MockitoBean OkHttpClient gabiaOkHttpClient;

  @Autowired WebApplicationContext context;
  @Autowired JdbcTemplate jdbc;
  @Autowired AppServiceBadgeService appServiceBadgeService;

  private MockMvc mvc;
  private LocalDateTime now;
  private long otherProfileId;

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
    profile(ME, "익명의 매운 떡볶이");
    otherProfileId = profile(OTHER, "익명의 시원한 냉면");
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

  private long profile(long userId, String nickname) {
    jdbc.update(
        "INSERT INTO sopt_letter_profile"
            + " (user_id, nickname, is_onboarded, has_written, created_at, updated_at)"
            + " VALUES (?, ?, true, false, ?, ?)",
        userId,
        nickname,
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
    jdbc.update("DELETE FROM sopt_letter_topic_read");
    jdbc.update("DELETE FROM sopt_letter");
    jdbc.update("DELETE FROM sopt_letter_topic");
    jdbc.update("DELETE FROM sopt_letter_profile");
  }
}
