package org.sopt.makers.api.controller.app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.resolver.CurrentUserIdArgumentResolver;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.api.controller.app.attendance.AppAttendanceController;
import org.sopt.makers.api.controller.app.lecture.AppLectureController;
import org.sopt.makers.api.controller.app.member.AppMemberController;
import org.sopt.makers.api.controller.app.schedule.AppScheduleController;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.*;
import org.sopt.makers.domain.admin.app.service.*;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.sopt.makers.domain.admin.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(AppOperationSecurityTest.Config.class)
@ActiveProfiles("prod")
class AppOperationSecurityTest {
  private static final String BASE = "/api/v1/app";

  @Autowired private WebApplicationContext context;
  @Autowired private AppLectureService lectures;
  @Autowired private AppMemberService members;
  @Autowired private AttendanceService attendances;
  @Autowired private AppScheduleService schedules;
  @Autowired private JwtAccessTokenService tokenService;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(lectures, members, attendances, schedules, tokenService);
    when(lectures.getTodayLecture(2L)).thenReturn(AppLectureResult.empty());
    when(lectures.getCurrentLectureRound(1L)).thenReturn(new AppSubLecture(2L, 1, null));
    when(members.getMemberTotalAttendance(2L))
        .thenReturn(
            new AppMemberAttendanceSummary(
                new Member(2L, "홍길동", 39, Part.SERVER, 2f, 0, 0, 0, 0), List.of()));
    when(members.getMemberScore(2L)).thenReturn(2f);
    when(attendances.attend(2L, 2L, "1234")).thenReturn(2L);
    when(schedules.getSchedules(any(), any())).thenReturn(Map.of());
    when(tokenService.parse("Bearer admin"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("1", "", List.of(new SimpleGrantedAuthority("ADMIN"))));
    when(tokenService.parse("Bearer user"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("2", "", List.of(new SimpleGrantedAuthority("USER"))));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @ParameterizedTest
  @MethodSource("appRoutes")
  @DisplayName("일반 사용자 토큰으로 앱용 API를 호출할 수 있다")
  void allowsUser(HttpMethod method, String path) throws Exception {
    mvc.perform(
            request(method, path)
                .header("Authorization", "Bearer user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subLectureId\":2,\"code\":\"1234\"}")
                .param("start", "2026-11-15T14:00:00")
                .param("end", "2026-11-15T18:00:00"))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("appRoutes")
  @DisplayName("앱용 API는 JWT 없이 호출할 수 없다")
  void requiresJwt(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path)).andExpect(status().isUnauthorized());
    verifyNoInteractions(lectures, members, attendances, schedules);
  }

  private static Stream<Arguments> appRoutes() {
    return Stream.of(
        Arguments.of(HttpMethod.GET, BASE + "/lectures"),
        Arguments.of(HttpMethod.GET, BASE + "/lectures/round/1"),
        Arguments.of(HttpMethod.POST, BASE + "/attendances/attend"),
        Arguments.of(HttpMethod.GET, BASE + "/members/attendances"),
        Arguments.of(HttpMethod.GET, BASE + "/members/score"),
        Arguments.of(HttpMethod.GET, BASE + "/schedules"));
  }

  @Configuration
  @EnableWebMvc
  @EnableSpringDataWebSupport
  @Import({
    SecurityConfig.class,
    AppLectureController.class,
    AppAttendanceController.class,
    AppMemberController.class,
    AppScheduleController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
      resolvers.add(new CurrentUserIdArgumentResolver());
    }

    @Bean
    AppLectureService lectures() {
      return mock(AppLectureService.class);
    }

    @Bean
    AppMemberService members() {
      return mock(AppMemberService.class);
    }

    @Bean
    AttendanceService attendances() {
      return mock(AttendanceService.class);
    }

    @Bean
    AppScheduleService schedules() {
      return mock(AppScheduleService.class);
    }

    @Bean
    JwtAccessTokenService tokenService() {
      return mock(JwtAccessTokenService.class);
    }

    @Bean
    ObjectMapper objectMapper() {
      return JsonMapper.builder().build();
    }

    @Bean
    SecurityProperty securityProperty() {
      return new SecurityProperty(
          new SecurityProperty.Api(Map.of("monitoring", "test-key"), List.of("/actuator")), null);
    }
  }
}
