package org.sopt.makers.api.controller.official.s3;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(S3ApiTest.Config.class)
@ActiveProfiles("prod")
class S3ApiTest {
  private static final String PATH = "/v2/s3/presigned-url";
  private static final String BODY =
      """
      {"fileName":"news.png","contentType":"image/png","directory":"news/"}
      """;
  @Autowired private WebApplicationContext context;
  @Autowired private NewsService service;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(service, tokens);
    when(tokens.parse("Bearer admin"))
        .thenAnswer(
            inv -> new CustomAuthentication("1", "", List.of(new SimpleGrantedAuthority("ADMIN"))));
    when(tokens.parse("Bearer member"))
        .thenAnswer(
            inv ->
                new CustomAuthentication("2", "", List.of(new SimpleGrantedAuthority("MEMBER"))));
    when(service.generatePresignedUrl(any()))
        .thenReturn(
            new NewsFileStoragePort.PresignedFile(
                "https://upload.example/news.png?signature=test",
                "https://file.example/news.png",
                600,
                "develop/news/id_news.png"));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("업로드 요청의 파일명과 콘텐츠 타입 및 디렉터리를 전달하고 URL 정보를 반환한다")
  void createsPresignedUrl() throws Exception {
    mvc.perform(
            post(PATH)
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.presignedUrl").value("https://upload.example/news.png?signature=test"))
        .andExpect(jsonPath("$.data.fileUrl").value("https://file.example/news.png"))
        .andExpect(jsonPath("$.data.expiresIn").value(600))
        .andExpect(jsonPath("$.data.fileKey").value("develop/news/id_news.png"));
    verify(service)
        .generatePresignedUrl(
            new NewsFileStoragePort.PresignedFileRequest("news.png", "image/png", "news/"));
  }

  @Test
  @DisplayName("디렉터리는 생략할 수 있다")
  void acceptsOmittedDirectory() throws Exception {
    mvc.perform(
            post(PATH)
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
        {"fileName":"news.png","contentType":"image/png"}
        """))
        .andExpect(status().isOk());
    verify(service)
        .generatePresignedUrl(
            new NewsFileStoragePort.PresignedFileRequest("news.png", "image/png", null));
  }

  @Test
  @DisplayName("일반 회원 JWT로도 URL을 발급한다")
  void acceptsMemberJwt() throws Exception {
    mvc.perform(
            post(PATH)
                .header("Authorization", "Bearer member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk());
    verify(service).generatePresignedUrl(any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"jpeg", "jpg", "png", "gif", "webp"})
  @DisplayName("허용된 이미지 콘텐츠 타입으로 URL을 발급한다")
  void acceptsImageTypes(String type) throws Exception {
    mvc.perform(
            post(PATH)
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fileName\":\"news.png\",\"contentType\":\"image/" + type + "\"}"))
        .andExpect(status().isOk());
    verify(service)
        .generatePresignedUrl(
            new NewsFileStoragePort.PresignedFileRequest("news.png", "image/" + type, null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"contentType\":\"image/png\"}",
        "{\"fileName\":\"\",\"contentType\":\"image/png\"}",
        "{\"fileName\":\"news.png\"}",
        "{\"fileName\":\"news.png\",\"contentType\":\"\"}",
        "{\"fileName\":\"file.pdf\",\"contentType\":\"application/pdf\"}",
        "{\"fileName\":\"news.png\",\"contentType\":\"IMAGE/PNG\"}"
      })
  @DisplayName("필수 항목이 없거나 허용되지 않은 콘텐츠 타입이면 발급하지 않는다")
  void rejectsInvalidRequest(String body) throws Exception {
    mvc.perform(
            post(PATH)
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("JWT 없이 업로드 URL을 발급할 수 없다")
  void requiresJwt() throws Exception {
    mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    S3Controller.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    NewsService service() {
      return mock(NewsService.class);
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
