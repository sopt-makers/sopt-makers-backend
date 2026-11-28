package org.sopt.makers.api.support;

import static org.mockito.Mockito.verify;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
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
public abstract class SoptLetterIntegrationTestSupport {

  protected static final String DB_URL = System.getenv("SOPT_LETTER_TEST_DB_URL");
  protected static final long ME = 9001L;
  protected static final long OTHER = 9002L;
  protected static final int CURRENT_GENERATION = 36;

  @MockitoBean OAuthAuthenticatorPort oAuthAuthenticatorPort;
  @MockitoBean TokenIssuerPort tokenIssuerPort;
  @MockitoBean SmsSenderPort smsSenderPort;
  @MockitoBean OkHttpClient gabiaOkHttpClient;
  @MockitoBean protected PushSenderPort pushSenderPort;

  @Autowired WebApplicationContext context;
  @Autowired protected JdbcTemplate jdbc;
  @Autowired EntityManager em;
  @Autowired PlatformTransactionManager transactionManager;

  protected MockMvc mvc;
  protected LocalDateTime now;
  protected long otherProfileId;
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
  void setUpSoptLetter() {
    cleanUp();
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
    loginAs(ME);
    now = LocalDateTime.now();
    profile(ME, "익명의 매운 떡볶이", false);
    otherProfileId = profile(OTHER, "익명의 시원한 냉면", false);
  }

  @AfterEach
  void tearDownSoptLetter() {
    SecurityContextHolder.clearContext();
    cleanUp();
  }

  protected void loginAs(long userId) {
    SecurityContextHolder.getContext()
        .setAuthentication(new CustomAuthentication(String.valueOf(userId), "", List.of()));
  }

  protected PushMessage sentPush() {
    ArgumentCaptor<PushMessage> captor = ArgumentCaptor.forClass(PushMessage.class);
    verify(pushSenderPort).send(captor.capture());
    return captor.getValue();
  }

  protected long user(int generation) {
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

  protected long profile(long userId, String nickname, boolean hasWritten) {
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

  protected long topic(
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

  protected long letter(long authorProfileId, long topicId, LocalDateTime createdAt) {
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
    jdbc.update("DELETE FROM app_service WHERE service_name = 'SOPT_LETTER'");
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
