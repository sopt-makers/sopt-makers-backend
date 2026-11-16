package org.sopt.makers.api.controller.official.news;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.common.config.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

@SpringJUnitWebConfig(NewsApiTest.Config.class)
@ActiveProfiles("prod")
class NewsApiTest {
  private static final String BASE = "/v2/admin/news";
  private static final String BODY =
      """
   {"imageUrl":"https://sopt.org/image.png","title":"소식","link":"https://sopt.org/article"}
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
    when(service.getById(1)).thenReturn(news());
    when(service.findAll()).thenReturn(List.of(news()));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("파일로 소식을 등록하면 업로드 파일을 전달하고 성공 메시지를 반환한다")
  void createsWithFile() throws Exception {
    mvc.perform(
            multipart(BASE)
                .file(image())
                .param("title", "소식")
                .param("link", "https://sopt.org/article")
                .header("Authorization", "Bearer admin"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("최신소식 추가에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    var captor = ArgumentCaptor.forClass(NewsService.CreateNewsWithFileCommand.class);
    verify(service).createWithFile(captor.capture());
    assertThat(captor.getValue().title()).isEqualTo("소식");
    assertThat(captor.getValue().link()).isEqualTo("https://sopt.org/article");
    assertThat(captor.getValue().image().originalFilename()).isEqualTo("news.png");
    assertThat(captor.getValue().image().contentType()).isEqualTo("image/png");
    assertThat(captor.getValue().image().inputStream().readAllBytes()).containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("이미지 URL로 소식을 등록하면 성공 메시지를 반환한다")
  void createsWithUrl() throws Exception {
    mvc.perform(
            post(BASE + "/v2")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("최신소식 추가에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    verify(service)
        .create(
            new NewsService.CreateNewsCommand(
                "https://sopt.org/image.png", "소식", "https://sopt.org/article"));
  }

  @Test
  @DisplayName("파일로 소식을 수정하면 새 파일을 전달하고 성공 메시지를 반환한다")
  void editsWithFile() throws Exception {
    mvc.perform(
            multipart(HttpMethod.PATCH, BASE + "/1")
                .file(image())
                .param("title", "수정")
                .param("link", "https://sopt.org/article")
                .header("Authorization", "Bearer admin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("최신소식 수정에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    var captor = ArgumentCaptor.forClass(NewsService.EditNewsWithFileCommand.class);
    verify(service).editWithFile(eq(1), captor.capture());
    assertThat(captor.getValue().image().originalFilename()).isEqualTo("news.png");
    assertThat(captor.getValue().title()).isEqualTo("수정");
  }

  @Test
  @DisplayName("수정 요청에서 이미지를 생략하거나 빈 파일이면 기존 이미지 유지 요청을 전달한다")
  void editsWithoutNewImage() throws Exception {
    mvc.perform(
            multipart(HttpMethod.PATCH, BASE + "/1")
                .param("title", "수정")
                .param("link", "https://sopt.org/article")
                .header("Authorization", "Bearer admin"))
        .andExpect(status().isOk());
    mvc.perform(
            multipart(HttpMethod.PATCH, BASE + "/1")
                .file(new MockMultipartFile("image", "", "image/png", new byte[0]))
                .param("title", "수정")
                .param("link", "https://sopt.org/article")
                .header("Authorization", "Bearer admin"))
        .andExpect(status().isOk());
    verify(service, times(2))
        .editWithFile(
            1, new NewsService.EditNewsWithFileCommand(null, "수정", "https://sopt.org/article"));
  }

  @Test
  @DisplayName("이미지 URL로 소식을 수정하면 성공 메시지를 반환한다")
  void editsWithUrl() throws Exception {
    mvc.perform(
            patch(BASE + "/1/v2")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("최신소식 수정에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    verify(service)
        .edit(
            1,
            new NewsService.EditNewsCommand(
                "https://sopt.org/image.png", "소식", "https://sopt.org/article"));
  }

  @Test
  @DisplayName("소식 삭제 요청은 본문의 식별자를 전달하고 성공 메시지를 반환한다")
  void deletesNews() throws Exception {
    mvc.perform(
            post(BASE + "/delete")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("최신소식 삭제에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    verify(service).delete(1);
  }

  @Test
  @DisplayName("소식 단건 조회는 이미지 필드를 image로 반환한다")
  void getsNews() throws Exception {
    mvc.perform(get(BASE + "/news").param("id", "1").header("Authorization", "Bearer admin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.image").value("https://sopt.org/image.png"))
        .andExpect(jsonPath("$.data.title").value("소식"))
        .andExpect(jsonPath("$.data.link").value("https://sopt.org/article"))
        .andExpect(jsonPath("$.data.imageUrl").doesNotExist());
    verify(service).getById(1);
  }

  @Test
  @DisplayName("통합 서버의 전체 소식 조회도 관리자 권한으로 조회한다")
  void listsNews() throws Exception {
    mvc.perform(get(BASE).header("Authorization", "Bearer admin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].image").value("https://sopt.org/image.png"));
    verify(service).findAll();
  }

  @ParameterizedTest
  @MethodSource("routes")
  @DisplayName("소식 관리 API는 JWT 없이 호출할 수 없다")
  void rejectsAnonymous(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, BASE + path).contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @MethodSource("routes")
  @DisplayName("일반 회원은 소식 관리 API를 호출할 수 없다")
  void rejectsMember(HttpMethod method, String path) throws Exception {
    mvc.perform(
            request(method, BASE + path)
                .header("Authorization", "Bearer member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"title\":\"소식\",\"link\":\"https://sopt.org\"}",
        "{\"imageUrl\":\"https://sopt.org/image\",\"link\":\"https://sopt.org\"}",
        "{\"imageUrl\":\"https://sopt.org/image\",\"title\":\"소식\"}"
      })
  @DisplayName("이미지 URL과 제목 및 링크가 없으면 등록과 수정 요청을 거부한다")
  void validatesUrlRequest(String body) throws Exception {
    mvc.perform(
            post(BASE + "/v2")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest());
    mvc.perform(
            patch(BASE + "/1/v2")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("파일 등록에 이미지가 없으면 요청을 거부한다")
  void requiresImage() throws Exception {
    mvc.perform(
            multipart(BASE)
                .param("title", "소식")
                .param("link", "https://sopt.org/article")
                .header("Authorization", "Bearer admin"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("소식 조회와 삭제에는 식별자가 필요하다")
  void requiresId() throws Exception {
    mvc.perform(get(BASE + "/news").header("Authorization", "Bearer admin"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post(BASE + "/delete")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  private MockMultipartFile image() {
    return new MockMultipartFile("image", "news.png", "image/png", new byte[] {1, 2, 3});
  }

  private News news() {
    return new News(
        1, "https://sopt.org/image.png", "소식", "https://sopt.org/article", null, false, null, null);
  }

  private static Stream<Arguments> routes() {
    return Stream.of(
        Arguments.of(HttpMethod.POST, ""),
        Arguments.of(HttpMethod.POST, "/v2"),
        Arguments.of(HttpMethod.PATCH, "/1"),
        Arguments.of(HttpMethod.PATCH, "/1/v2"),
        Arguments.of(HttpMethod.POST, "/delete"),
        Arguments.of(HttpMethod.GET, "/news"),
        Arguments.of(HttpMethod.GET, ""));
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    NewsController.class,
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
