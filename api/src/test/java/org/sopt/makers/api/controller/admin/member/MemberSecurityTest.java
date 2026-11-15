package org.sopt.makers.api.controller.admin.member;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.resolver.CurrentUserIdArgumentResolver;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.api.controller.playground.user.PlaygroundUserController;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.sopt.makers.domain.admin.member.service.AdminMemberService;
import org.sopt.makers.domain.playground.member.ask.service.UserAskQueryService;
import org.sopt.makers.domain.playground.member.profile.UserInfo;
import org.sopt.makers.domain.playground.member.profile.UserSummary;
import org.sopt.makers.domain.playground.member.profile.service.*;
import org.sopt.makers.domain.playground.member.relation.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
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

@SpringJUnitWebConfig(MemberSecurityTest.Config.class)
@ActiveProfiles("prod")
class MemberSecurityTest {
  private static final String BASE = "/api/v1/members";

  @Autowired private WebApplicationContext context;
  @Autowired private AdminMemberService service;
  @Autowired private UserProfileQueryService userProfiles;
  @Autowired private JwtAccessTokenService tokenService;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(service, tokenService, userProfiles);
    when(tokenService.parse("Bearer admin"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("1", "", List.of(new SimpleGrantedAuthority("ADMIN"))));
    when(tokenService.parse("Bearer user"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("2", "", List.of(new SimpleGrantedAuthority("MEMBER"))));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("ADMIN 권한이 있는 사용자는 회원 및 출석 관리 API에 접근할 수 있다")
  void allowsAdmin() throws Exception {
    when(service.getMembers(39, null, 0, 20)).thenReturn(List.of());
    mvc.perform(
            get(BASE + "/list").param("generation", "39").header("Authorization", "Bearer admin"))
        .andExpect(status().isOk());
    verify(service).getMembers(39, null, 0, 20);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("일반 사용자 토큰으로는 회원 및 출석 관리 API에 접근할 수 없다")
  void rejectsNonAdmin(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path).header("Authorization", "Bearer user"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("회원 및 출석 관리 API는 JWT 없이 접근할 수 없다")
  void requiresJwt(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path)).andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("일반 회원 토큰으로 내 정보를 조회할 수 있다")
  void allowsMemberProfile() throws Exception {
    when(userProfiles.getMyInfo(2L))
        .thenReturn(
            new UserInfo(new UserSummary(2L, "홍길동", 39, null, true, false), false, false, false));
    mvc.perform(get(BASE + "/me").header("Authorization", "Bearer user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(2));
    verify(userProfiles).getMyInfo(2L);
  }

  @Test
  @DisplayName("일반 회원 토큰으로 회원을 검색할 수 있다")
  void allowsMemberSearch() throws Exception {
    when(userProfiles.searchByName("홍길동")).thenReturn(List.of());
    mvc.perform(get(BASE + "/search").param("name", "홍길동").header("Authorization", "Bearer user"))
        .andExpect(status().isOk());
    verify(userProfiles).searchByName("홍길동");
  }

  @ParameterizedTest
  @ValueSource(strings = {"/me", "/search"})
  @DisplayName("일반 회원 API도 JWT 없이 호출할 수 없다")
  void requiresMemberJwt(String path) throws Exception {
    mvc.perform(get(BASE + path)).andExpect(status().isUnauthorized());
    verifyNoInteractions(userProfiles);
  }

  private static Stream<Arguments> adminRoutes() {
    return Stream.of(
        Arguments.of(HttpMethod.GET, BASE + "/list"),
        Arguments.of(HttpMethod.GET, "/api/v1/attendances/10"),
        Arguments.of(HttpMethod.PATCH, "/api/v1/attendances/member/10"),
        Arguments.of(HttpMethod.PATCH, "/api/v1/attendances"),
        Arguments.of(HttpMethod.GET, "/api/v1/attendances/lecture/1"));
  }

  @Configuration
  @EnableWebMvc
  @EnableSpringDataWebSupport
  @Import({
    SecurityConfig.class,
    MemberController.class,
    org.sopt.makers.api.controller.admin.attendance.AdminAttendanceController.class,
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
    UserProfileQueryService userProfiles() {
      return mock(UserProfileQueryService.class);
    }

    @Bean
    PlaygroundUserController playgroundUserController(UserProfileQueryService queryService) {
      return new PlaygroundUserController(
          queryService,
          mock(UserProfileCommandService.class),
          mock(UserProfileListService.class),
          mock(UserRecommendationService.class),
          mock(AppJamTlService.class),
          mock(UserAskQueryService.class),
          mock(UserRelationService.class),
          mock(UserPropertyService.class));
    }

    @Bean
    AttendanceService attendanceService() {
      return mock(AttendanceService.class);
    }

    @Bean
    AdminMemberService memberService() {
      return mock(AdminMemberService.class);
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
