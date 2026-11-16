package org.sopt.makers.api.controller.official.soptstory;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.service.SoptStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
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

@SpringJUnitWebConfig(SoptStoryApiTest.Config.class)
@ActiveProfiles("prod")
@TestPropertySource(properties = "official.apikey=official-test-key")
class SoptStoryApiTest {
  private static final String BASE = "/v2/soptstory";
  private static final String BODY =
      """
      {"link":"https://sopt.org/story"}
      """;
  @Autowired private WebApplicationContext context;
  @Autowired private SoptStoryService service;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(service, tokens);
    when(service.createSoptStory(anyString()))
        .thenReturn(
            new ScrapedArticle("thumbnail", "제목", "설명", "https://sopt.org/article", "blog"));
    when(service.getSoptStoryList(any(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of()));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("API 키로 솝트스토리를 생성하면 스크래핑 결과를 반환한다")
  void createsStory() throws Exception {
    mvc.perform(
            post(BASE)
                .header("api-key", "official-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.thumbnailUrl").value("thumbnail"))
        .andExpect(jsonPath("$.data.title").value("제목"))
        .andExpect(jsonPath("$.data.description").value("설명"))
        .andExpect(jsonPath("$.data.soptStoryUrl").value("https://sopt.org/article"))
        .andExpect(jsonPath("$.data.articleUrl").doesNotExist());
    verify(service).createSoptStory("https://sopt.org/story");
    verifyNoInteractions(tokens);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "wrong-key"})
  @DisplayName("API 키가 비어 있거나 다르면 솝트스토리를 생성할 수 없다")
  void rejectsInvalidKey(String key) throws Exception {
    mvc.perform(
            post(BASE).header("api-key", key).contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("API 키 헤더가 생략되면 솝트스토리를 생성할 수 없다")
  void rejectsMissingKey() throws Exception {
    mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("링크가 비어 있거나 500자를 초과하면 생성 요청을 거부한다")
  void validatesLink() throws Exception {
    mvc.perform(
            post(BASE)
                .header("api-key", "official-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"link\":\"\"}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post(BASE)
                .header("api-key", "official-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"link\":\"" + "a".repeat(501) + "\"}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("생성 요청의 링크는 500자까지 허용한다")
  void acceptsMaximumLinkLength() throws Exception {
    mvc.perform(
            post(BASE)
                .header("api-key", "official-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"link\":\"" + "a".repeat(500) + "\"}"))
        .andExpect(status().isOk());
    verify(service).createSoptStory("a".repeat(500));
  }

  @Test
  @DisplayName("목록은 공개 조회하며 URL과 등록 일시 및 사용자 좋아요 여부를 반환한다")
  void listsStories() throws Exception {
    var time = LocalDateTime.of(2026, 11, 16, 12, 30);
    var liked = new SoptStory(1L, "제목", "설명", "thumbnail", "https://sopt.org/article", 3, time);
    var notLiked = new SoptStory(2L, "다른 글", "설명", "thumbnail", "https://sopt.org/other", 0, time);
    var stories = List.of(liked, notLiked);
    when(service.getSoptStoryList("recent", 1, 10))
        .thenReturn(new PageImpl<>(stories, PageRequest.of(0, 10), 2));
    when(service.getLikedSoptStoryIds("203.0.113.1", stories)).thenReturn(Set.of(1L));
    mvc.perform(get(BASE).header("X-Forwarded-For", "203.0.113.1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].url").value("https://sopt.org/article"))
        .andExpect(jsonPath("$.data.content[0].uploadedAt").exists())
        .andExpect(jsonPath("$.data.content[0].likeCount").value(3))
        .andExpect(jsonPath("$.data.content[0].isLikedByUser").value(true))
        .andExpect(jsonPath("$.data.content[1].isLikedByUser").value(false))
        .andExpect(jsonPath("$.data.content[0].articleUrl").doesNotExist())
        .andExpect(jsonPath("$.data.content[0].createdAt").doesNotExist())
        .andExpect(jsonPath("$.data.content[0].isLiked").doesNotExist())
        .andExpect(jsonPath("$.data.totalCount").value(2))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.currentPage").value(1))
        .andExpect(jsonPath("$.data.totalPages").value(1))
        .andExpect(jsonPath("$.data.hasNext").value(false))
        .andExpect(jsonPath("$.data.hasPrevious").value(false));
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("정렬과 페이지 조건을 목록 조회에 전달한다")
  void passesSearchOptions() throws Exception {
    mvc.perform(get(BASE).param("sort", "likes").param("pageNo", "2").param("limit", "5"))
        .andExpect(status().isOk());
    verify(service).getSoptStoryList("likes", 2, 5);
  }

  @Test
  @DisplayName("원격 주소가 없으면 루프백 주소로 목록을 조회한다")
  void listsWithoutIp() throws Exception {
    mvc.perform(
            get(BASE)
                .with(
                    request -> {
                      request.setRemoteAddr(null);
                      return request;
                    }))
        .andExpect(status().isOk());
    verify(service).getSoptStoryList("recent", 1, 10);
    verify(service).getLikedSoptStoryIds("127.0.0.1", List.of());
  }

  @Test
  @DisplayName("페이지 번호와 크기의 허용 범위를 검증한다")
  void validatesPagination() throws Exception {
    mvc.perform(get(BASE).param("pageNo", "0")).andExpect(status().isBadRequest());
    mvc.perform(get(BASE).param("limit", "0")).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("좋아요는 클라이언트 IP로 처리하고 좋아요 식별자를 반환한다")
  void likesStory() throws Exception {
    when(service.like(1L, "203.0.113.1")).thenReturn(10L);
    mvc.perform(post(BASE + "/1/like").header("X-Forwarded-For", "203.0.113.1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(10))
        .andExpect(jsonPath("$.data.soptStoryId").value(1))
        .andExpect(jsonPath("$.data.ip").value("203.0.113.1"));
    verify(service).like(1L, "203.0.113.1");
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("좋아요 취소는 원격 주소로 처리하고 취소한 좋아요 식별자를 반환한다")
  void unlikesStory() throws Exception {
    when(service.unlike(1L, "203.0.113.2")).thenReturn(10L);
    mvc.perform(
            post(BASE + "/1/unlike")
                .with(
                    request -> {
                      request.setRemoteAddr("203.0.113.2");
                      return request;
                    }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(10))
        .andExpect(jsonPath("$.data.soptStoryId").value(1))
        .andExpect(jsonPath("$.data.ip").value("203.0.113.2"));
    verify(service).unlike(1L, "203.0.113.2");
  }

  @Test
  @DisplayName("전달된 프록시 주소 목록에서는 첫 번째 IP로 좋아요를 처리한다")
  void usesFirstForwardedIp() throws Exception {
    mvc.perform(post(BASE + "/1/like").header("X-Forwarded-For", "203.0.113.1, 203.0.113.2"))
        .andExpect(status().isOk());
    verify(service).like(1L, "203.0.113.1");
  }

  @Test
  @DisplayName("유효하지 않은 클라이언트 IP면 좋아요 요청을 거부한다")
  void rejectsLikeWithInvalidIp() throws Exception {
    mvc.perform(
            post(BASE + "/1/like")
                .with(
                    request -> {
                      request.setRemoteAddr("invalid-ip");
                      return request;
                    }))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("다른 비공개 API 경로는 JWT 없이 접근할 수 없다")
  void keepsOtherRoutesProtected() throws Exception {
    mvc.perform(get("/v2/soptstory-extra")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("중간 페이지는 올림한 전체 페이지 수와 이전 및 다음 페이지 여부를 반환한다")
  void returnsMiddlePageMetadata() throws Exception {
    when(service.getSoptStoryList("recent", 2, 10))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 25));
    mvc.perform(get(BASE).param("pageNo", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.totalCount").value(25))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.currentPage").value(2))
        .andExpect(jsonPath("$.data.totalPages").value(3))
        .andExpect(jsonPath("$.data.hasNext").value(true))
        .andExpect(jsonPath("$.data.hasPrevious").value(true));
  }

  @Test
  @DisplayName("마지막 페이지에는 다음 페이지가 없다")
  void returnsLastPageMetadata() throws Exception {
    when(service.getSoptStoryList("recent", 3, 10))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 10), 25));
    mvc.perform(get(BASE).param("pageNo", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalPages").value(3))
        .andExpect(jsonPath("$.data.hasNext").value(false))
        .andExpect(jsonPath("$.data.hasPrevious").value(true));
  }

  @Test
  @DisplayName("조회 결과가 없으면 전체 페이지 수는 0이고 이전 및 다음 페이지가 없다")
  void returnsEmptyPageMetadata() throws Exception {
    mvc.perform(get(BASE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").isEmpty())
        .andExpect(jsonPath("$.data.totalCount").value(0))
        .andExpect(jsonPath("$.data.totalPages").value(0))
        .andExpect(jsonPath("$.data.hasNext").value(false))
        .andExpect(jsonPath("$.data.hasPrevious").value(false));
  }

  @Test
  @DisplayName("목록 크기는 100을 초과해도 조회할 수 있다")
  void acceptsLargePageSize() throws Exception {
    mvc.perform(get(BASE).param("limit", "101")).andExpect(status().isOk());
    verify(service).getSoptStoryList("recent", 1, 101);
  }

  @Test
  @DisplayName("공백 정렬 조건은 최신순으로 조회한다")
  void defaultsBlankSort() throws Exception {
    mvc.perform(get(BASE).param("sort", " ")).andExpect(status().isOk());
    verify(service).getSoptStoryList("recent", 1, 10);
  }

  @Test
  @DisplayName("전달 주소는 실제 IP 헤더보다 우선한다")
  void prioritizesForwardedIp() throws Exception {
    mvc.perform(
            post(BASE + "/1/like")
                .header("X-Real-IP", "203.0.113.2")
                .header("X-Forwarded-For", "203.0.113.1"))
        .andExpect(status().isOk());
    verify(service).like(1L, "203.0.113.1");
  }

  @Test
  @DisplayName("알 수 없는 전달 주소는 다음 프록시 헤더로 대체한다")
  void skipsUnknownIp() throws Exception {
    mvc.perform(
            post(BASE + "/1/like")
                .header("X-Forwarded-For", "unknown")
                .header("Proxy-Client-IP", "203.0.113.3"))
        .andExpect(status().isOk());
    verify(service).like(1L, "203.0.113.3");
  }

  @Test
  @DisplayName("IPv6 루프백 주소를 정규화해 좋아요를 처리한다")
  void normalizesLoopback() throws Exception {
    mvc.perform(
            post(BASE + "/1/like")
                .with(
                    request -> {
                      request.setRemoteAddr("0:0:0:0:0:0:0:1");
                      return request;
                    }))
        .andExpect(status().isOk());
    verify(service).like(1L, "::1");
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid-ip", "256.0.0.1"})
  @DisplayName("유효하지 않은 IP로는 목록을 조회할 수 없다")
  void rejectsListWithInvalidIp(String ip) throws Exception {
    mvc.perform(get(BASE).header("X-Forwarded-For", ip)).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2001:db8::1", "::ffff:192.0.2.1"})
  @DisplayName("축약형과 IPv4 혼합형 IPv6 주소로 좋아요를 처리한다")
  void acceptsIpv6Literals(String ip) throws Exception {
    mvc.perform(post(BASE + "/1/like").header("X-Forwarded-For", ip)).andExpect(status().isOk());
    verify(service).like(1L, ip);
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    SoptStoryController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    SoptStoryService service() {
      return mock(SoptStoryService.class);
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
