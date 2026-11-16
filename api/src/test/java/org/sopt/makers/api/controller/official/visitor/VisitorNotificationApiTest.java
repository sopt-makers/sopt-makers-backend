package org.sopt.makers.api.controller.official.visitor;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.api.controller.official.notification.NotificationController;
import org.sopt.makers.domain.official.notification.Notification;
import org.sopt.makers.domain.official.notification.exception.*;
import org.sopt.makers.domain.official.notification.service.NotificationService;
import org.sopt.makers.domain.official.visitor.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(VisitorNotificationApiTest.Config.class)
@ActiveProfiles("prod")
class VisitorNotificationApiTest {
  private static final String BODY =
      """
      {"email":"test@sopt.org","generation":39}
      """;
  @Autowired private WebApplicationContext context;
  @Autowired private VisitorService visitors;
  @Autowired private NotificationService notifications;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(visitors, notifications, tokens);
    when(notifications.findByGeneration(anyInt())).thenReturn(List.of());
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("방문 등록은 IP와 사용자 에이전트를 전달하고 성공 상태를 반환한다")
  void registersVisitor() throws Exception {
    mvc.perform(
            post("/v2/visitor").header("X-Real-IP", "203.0.113.1").header("User-Agent", "browser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("Success"));
    verify(visitors).markVisited("203.0.113.1", "browser");
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("프록시 주소 목록에서는 첫 번째 IP를 방문자로 전달한다")
  void usesFirstForwardedIp() throws Exception {
    mvc.perform(post("/v2/visitor").header("X-Forwarded-For", "203.0.113.1, 203.0.113.2"))
        .andExpect(status().isOk());
    verify(visitors).markVisited("203.0.113.1", "");
  }

  @Test
  @DisplayName("프록시 헤더가 없으면 원격 주소를 방문자로 전달한다")
  void usesRemoteAddress() throws Exception {
    mvc.perform(
            post("/v2/visitor")
                .with(
                    request -> {
                      request.setRemoteAddr("203.0.113.3");
                      return request;
                    }))
        .andExpect(status().isOk());
    verify(visitors).markVisited("203.0.113.3", "");
  }

  @Test
  @DisplayName("오늘 방문자 수는 인증 없이 조회한다")
  void getsTodayCount() throws Exception {
    when(visitors.getTodayCount()).thenReturn(42);
    mvc.perform(get("/v2/visitor"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.count").value(42));
    verify(visitors).getTodayCount();
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("방문자가 없으면 오늘 방문자 수는 0이다")
  void getsEmptyCount() throws Exception {
    mvc.perform(get("/v2/visitor"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.count").value(0));
  }

  @Test
  @DisplayName("모집 알림 신청은 등록 정보와 200 응답을 반환한다")
  void registersNotification() throws Exception {
    when(notifications.register(
            new NotificationService.RegisterNotificationCommand("test@sopt.org", 39)))
        .thenReturn(
            new Notification(1L, "test@sopt.org", 39, LocalDateTime.of(2026, 11, 16, 12, 30)));
    mvc.perform(
            post("/v2/notification/register").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("test@sopt.org"))
        .andExpect(jsonPath("$.data.generation").value(39))
        .andExpect(jsonPath("$.data.createdAt").exists());
    verify(notifications)
        .register(new NotificationService.RegisterNotificationCommand("test@sopt.org", 39));
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("같은 이메일과 기수로 중복 신청하면 409를 반환한다")
  void rejectsDuplicateNotification() throws Exception {
    when(notifications.register(any()))
        .thenThrow(new NotificationException(NotificationFailure.DUPLICATE_NOTIFICATION));
    mvc.perform(
            post("/v2/notification/register").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isConflict());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"email\":\"invalid\",\"generation\":39}",
        "{\"email\":\"\",\"generation\":39}",
        "{\"generation\":39}",
        "{\"email\":\"test@sopt.org\",\"generation\":0}",
        "{\"email\":\"test@sopt.org\",\"generation\":-1}",
        "{\"email\":\"test@sopt.org\"}"
      })
  @DisplayName("이메일이나 기수가 올바르지 않으면 신청을 거부한다")
  void validatesNotification(String body) throws Exception {
    mvc.perform(
            post("/v2/notification/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(notifications);
  }

  @Test
  @DisplayName("기수별 모집 알림 목록은 이메일 목록을 반환한다")
  void listsNotifications() throws Exception {
    when(notifications.findByGeneration(39))
        .thenReturn(
            List.of(
                new Notification(1L, "one@sopt.org", 39, null),
                new Notification(2L, "two@sopt.org", 39, null)));
    mvc.perform(get("/v2/notification/list").param("generation", "39"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.generation").value(39))
        .andExpect(jsonPath("$.data.emailList[0]").value("one@sopt.org"))
        .andExpect(jsonPath("$.data.emailList[1]").value("two@sopt.org"));
    verify(notifications).findByGeneration(39);
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("신청 내역이 없으면 기수는 null이고 이메일 목록은 비어 있다")
  void listsEmptyNotifications() throws Exception {
    mvc.perform(get("/v2/notification/list").param("generation", "39"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.generation").value(nullValue()))
        .andExpect(jsonPath("$.data.emailList").isEmpty());
  }

  @Test
  @DisplayName("모집 알림 목록 조회에는 숫자 형태의 기수가 필요하다")
  void requiresGeneration() throws Exception {
    mvc.perform(get("/v2/notification/list")).andExpect(status().isBadRequest());
    mvc.perform(get("/v2/notification/list").param("generation", "invalid"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(notifications);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/v2/visitor-extra", "/v2/notification-extra"})
  @DisplayName("추가된 공개 경로 밖의 API는 JWT 없이 접근할 수 없다")
  void keepsOtherRoutesProtected(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isUnauthorized());
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    VisitorController.class,
    NotificationController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    VisitorService visitors() {
      return mock(VisitorService.class);
    }

    @Bean
    NotificationService notifications() {
      return mock(NotificationService.class);
    }

    @Bean
    JwtAccessTokenService tokens() {
      return mock(JwtAccessTokenService.class);
    }

    @Bean
    ObjectMapper objectMapper() {
      return JsonMapper.builder().build();
    }

    @Bean
    SecurityProperty securityProperty() {
      return new SecurityProperty(new SecurityProperty.Api(Map.of(), List.of()), null);
    }
  }
}
