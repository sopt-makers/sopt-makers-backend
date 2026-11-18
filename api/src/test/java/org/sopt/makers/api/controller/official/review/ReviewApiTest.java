package org.sopt.makers.api.controller.official.review;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.api.controller.official.homepage.review.HomepageReviewController;
import org.sopt.makers.domain.official.homepage.review.*;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.sopt.makers.domain.official.review.*;
import org.sopt.makers.domain.official.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(ReviewApiTest.Config.class)
@ActiveProfiles("prod")
@TestPropertySource(properties = "official.apikey=official-test-key")
class ReviewApiTest {
  private static final String CREATE =
      """
     {"generation":39,"part":"SERVER","mainCategory":"전체 활동","subActivities":["세미나"],"author":"김솝트","link":"https://sopt.org/review"}
     """;
  @Autowired private WebApplicationContext context;
  @Autowired private ReviewService reviews;
  @Autowired private HomepageReviewService homepage;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(reviews, homepage, tokens);
    when(reviews.search(any(), anyLong(), anyInt())).thenReturn(List.of());
    when(reviews.getRandomReviewsByPart()).thenReturn(List.of());
    when(homepage.getReviews()).thenReturn(List.of());
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("활동 후기 목록은 인증 없이 검색 조건과 페이지로 조회한다")
  void searchesReviews() throws Exception {
    mvc.perform(
            get("/v2/reviews")
                .param("part", "서버")
                .param("category", "전체 활동")
                .param("activity", "세미나")
                .param("generation", "39")
                .param("pageNo", "2")
                .param("limit", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.currentPage").value(2))
        .andExpect(jsonPath("$.data.limit").value(5));
    var condition =
        new ReviewSearchCondition("전체 활동", "세미나", org.sopt.makers.core.type.Part.SERVER, 39);
    verify(reviews).search(condition, 5L, 5);
    verify(reviews).count(condition);
    verifyNoInteractions(tokens);
  }

  @ParameterizedTest(name = "{0}: totalCount={1}, pageNo={2}")
  @CsvSource({
    "첫 페이지, 25, 1, 3, true, false",
    "중간 페이지, 25, 2, 3, true, true",
    "마지막 페이지, 25, 3, 3, false, true",
    "결과 0건, 0, 1, 0, false, false"
  })
  @DisplayName("활동 후기 목록은 레거시와 같은 페이지 정보를 응답한다")
  void reviewPageInfo(
      String label,
      long totalCount,
      int pageNo,
      int totalPage,
      boolean hasNextPage,
      boolean hasPrevPage)
      throws Exception {
    when(reviews.count(any())).thenReturn(totalCount);

    mvc.perform(get("/v2/reviews").param("pageNo", String.valueOf(pageNo)).param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalCount").value(totalCount))
        .andExpect(jsonPath("$.data.limit").value(10))
        .andExpect(jsonPath("$.data.pageNo").doesNotExist())
        .andExpect(jsonPath("$.data.currentPage").value(pageNo))
        .andExpect(jsonPath("$.data.totalPage").value(totalPage))
        .andExpect(jsonPath("$.data.hasNextPage").value(hasNextPage))
        .andExpect(jsonPath("$.data.hasPrevPage").value(hasPrevPage));
  }

  @Test
  @DisplayName("파트별 무작위 후기 목록은 인증 없이 조회한다")
  void randomReviews() throws Exception {
    mvc.perform(get("/v2/reviews/random"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray());
    verify(reviews).getRandomReviewsByPart();
  }

  @Test
  @DisplayName("올바른 API 키로 활동 후기를 등록하면 성공 문자열을 반환한다")
  void createsReview() throws Exception {
    mvc.perform(
            post("/v2/reviews")
                .header("api-key", "official-test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value("Success"));
    verify(reviews).create(any());
    verifyNoInteractions(tokens);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "wrong-key"})
  @DisplayName("API 키가 없거나 다르면 후기 등록을 거부한다")
  void rejectsInvalidApiKey(String key) throws Exception {
    mvc.perform(
            post("/v2/reviews")
                .header("api-key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(reviews);
  }

  @Test
  @DisplayName("API 키 헤더가 생략되면 후기 등록을 거부한다")
  void rejectsMissingHeader() throws Exception {
    mvc.perform(post("/v2/reviews").contentType(MediaType.APPLICATION_JSON).content(CREATE))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(reviews);
  }

  @Test
  @DisplayName("페이지 범위를 벗어나는 활동 후기 조회는 400을 반환한다")
  void validatesReviewPagination() throws Exception {
    mvc.perform(get("/v2/reviews").param("limit", "101")).andExpect(status().isBadRequest());
    mvc.perform(get("/v2/reviews").param("pageNo", "0")).andExpect(status().isBadRequest());
    verifyNoInteractions(reviews);
  }

  @Test
  @DisplayName("홈페이지 후기 생성은 유형을 전달하고 성공 메시지를 반환한다")
  void createsHomepageReview() throws Exception {
    mvc.perform(
            post("/v2/homepage-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"후기","content":"내용","authorInfo":"작성자","type":"RECRUITING"}
     """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("홈페이지 리뷰 추가에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    verify(homepage)
        .create(
            new HomepageReviewService.CreateHomepageReviewCommand(
                "후기", "내용", "작성자", HomepageReviewType.RECRUITING));
  }

  @Test
  @DisplayName("홈페이지 후기 생성 요청은 유형을 생략할 수 있다")
  void createsWithoutType() throws Exception {
    mvc.perform(
            post("/v2/homepage-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"후기","content":"내용","authorInfo":"작성자"}
     """))
        .andExpect(status().isCreated());
    verify(homepage)
        .create(new HomepageReviewService.CreateHomepageReviewCommand("후기", "내용", "작성자", null));
  }

  @Test
  @DisplayName("홈페이지 후기 목록은 유형과 작성자 정보를 포함한다")
  void listsHomepageReviews() throws Exception {
    when(homepage.getReviews())
        .thenReturn(
            List.of(
                new HomepageReview(
                    1L, HomepageReviewType.RECRUITING, "후기", "내용", "작성자", null, null)));
    mvc.perform(get("/v2/homepage-reviews"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].type").value("RECRUITING"))
        .andExpect(jsonPath("$.data[0].title").value("후기"))
        .andExpect(jsonPath("$.data[0].content").value("내용"))
        .andExpect(jsonPath("$.data[0].authorInfo").value("작성자"));
  }

  @Test
  @DisplayName("홈페이지 후기 수정 시 생략한 유형을 그대로 서비스에 전달한다")
  void editsWithoutType() throws Exception {
    mvc.perform(
            patch("/v2/homepage-reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"수정","content":"내용","authorInfo":"작성자"}
     """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("홈페이지 리뷰 수정에 성공했습니다."))
        .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    verify(homepage)
        .edit(1L, new HomepageReviewService.EditHomepageReviewCommand("수정", "내용", "작성자", null));
  }

  @Test
  @DisplayName("홈페이지 후기 수정 요청에 변경할 유형을 전달할 수 있다")
  void editsType() throws Exception {
    mvc.perform(
            patch("/v2/homepage-reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"수정","content":"내용","authorInfo":"작성자","type":"ACTIVITY"}
     """))
        .andExpect(status().isOk());
    verify(homepage)
        .edit(
            1L,
            new HomepageReviewService.EditHomepageReviewCommand(
                "수정", "내용", "작성자", HomepageReviewType.ACTIVITY));
  }

  @Test
  @DisplayName("홈페이지 후기 제목 길이와 유형이 잘못되면 400을 반환한다")
  void rejectsInvalidHomepageRequest() throws Exception {
    mvc.perform(
            post("/v2/homepage-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"12345678901","content":"내용","authorInfo":"작성자"}
     """))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/v2/homepage-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
     {"title":"후기","content":"내용","authorInfo":"작성자","type":"INVALID"}
     """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(homepage);
  }

  @Test
  @DisplayName("후기 외의 비공개 경로는 JWT 없이 접근할 수 없다")
  void keepsOtherRoutesProtected() throws Exception {
    mvc.perform(get("/v2/private")).andExpect(status().isUnauthorized());
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    ReviewController.class,
    HomepageReviewController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    ReviewService reviews() {
      return mock(ReviewService.class);
    }

    @Bean
    HomepageReviewService homepage() {
      return mock(HomepageReviewService.class);
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
