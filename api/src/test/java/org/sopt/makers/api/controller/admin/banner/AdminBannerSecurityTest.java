package org.sopt.makers.api.controller.admin.banner;

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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.admin.banner.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(AdminBannerSecurityTest.Config.class)
@ActiveProfiles("prod")
@TestPropertySource(properties = "official.apikey=test-official-key")
class AdminBannerSecurityTest {
  private static final String BASE = "/api/v1/banners";

  @Autowired private WebApplicationContext context;
  @Autowired private BannerService service;
  @Autowired private JwtAccessTokenService tokenService;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(service, tokenService);
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

  @Test
  @DisplayName("ADMIN 권한이 있는 사용자는 배너 관리 API에 접근할 수 있다")
  void allowsAdmin() throws Exception {
    when(service.getBannersWithPagination(
            BannerService.FilterCriteria.ALL, BannerService.SortCriteria.STATUS, 1, 10))
        .thenReturn(new BannerService.BannerPage<>(List.of(), 0, 10, 1));

    mvc.perform(get(BASE).header("Authorization", "Bearer admin")).andExpect(status().isOk());
    verify(service)
        .getBannersWithPagination(
            BannerService.FilterCriteria.ALL, BannerService.SortCriteria.STATUS, 1, 10);
  }

  @Test
  @DisplayName("외부 배너 이미지는 JWT 없이 유효한 api-key로 접근할 수 있다")
  void allowsExternalImagesWithoutJwt() throws Exception {
    when(service.getExternalBanners("cr_main")).thenReturn(List.of());

    mvc.perform(
            get(BASE + "/images")
                .param("location", "cr_main")
                .header("api-key", "test-official-key"))
        .andExpect(status().isOk());
    verify(service).getExternalBanners("cr_main");
    verifyNoInteractions(tokenService);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "wrong-key")
  @DisplayName("외부 배너 이미지 조회는 api-key가 없거나 유효하지 않으면 거부한다")
  void rejectsInvalidExternalKey(String key) throws Exception {
    var request = get(BASE + "/images").param("location", "cr_main");
    if (key != null) {
      request.header("api-key", key);
    }
    mvc.perform(request).andExpect(status().isUnauthorized());
    verifyNoInteractions(service, tokenService);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("일반 사용자 토큰으로는 배너 관리 API에 접근할 수 없다")
  void rejectsNonAdmin(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path).header("Authorization", "Bearer user"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("배너 관리 API는 JWT 없이 접근할 수 없다")
  void requiresJwt(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path)).andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  private static Stream<Arguments> adminRoutes() {
    return Stream.of(
        Arguments.of(HttpMethod.GET, BASE),
        Arguments.of(HttpMethod.GET, BASE + "/1"),
        Arguments.of(HttpMethod.POST, BASE),
        Arguments.of(HttpMethod.PUT, BASE + "/1"),
        Arguments.of(HttpMethod.DELETE, BASE + "/1"));
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    AdminBannerController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    BannerService bannerService() {
      return mock(BannerService.class);
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
