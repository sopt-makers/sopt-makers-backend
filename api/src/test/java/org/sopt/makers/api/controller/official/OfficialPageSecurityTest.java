package org.sopt.makers.api.controller.official;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.api.controller.official.homepage.HomepageController;
import org.sopt.makers.api.controller.official.project.ProjectController;
import org.sopt.makers.api.controller.official.recruit.RecruitPageController;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.homepage.facade.HomepageFacade;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.recruit.facade.RecruitPageFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(OfficialPageSecurityTest.Config.class)
@ActiveProfiles("prod")
class OfficialPageSecurityTest {
  @Autowired private WebApplicationContext context;
  @Autowired private HomepageFacade homepage;
  @Autowired private RecruitPageFacade recruit;
  @Autowired private ProjectService projects;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(homepage, recruit, projects, tokens);
    Generation generation = new Generation(39, "SOPT", null, null, null, null);
    when(homepage.getBrandingGeneration()).thenReturn(generation);
    when(homepage.getMainPage())
        .thenReturn(
            new HomepageFacade.MainPageData(
                generation,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new HomepageFacade.ActivitiesRecords(0, 0, 0, 0, 0, 0),
                List.of(),
                List.of()));
    when(homepage.getAboutPage())
        .thenReturn(
            new HomepageFacade.AboutPageData(
                generation, List.of(), List.of(), List.of(), List.of()));
    when(homepage.getRecruitPage())
        .thenReturn(
            new HomepageFacade.RecruitPageData(generation, List.of(), List.of(), List.of()));
    when(recruit.getRecruitMainPage())
        .thenReturn(
            new RecruitPageFacade.RecruitMainPage(generation, List.of(), List.of(), List.of()));
    when(recruit.getRecruitPartDetail(any()))
        .thenReturn(new RecruitPageFacade.RecruitPartDetail("파트 소개", List.of(), List.of()));
    when(projects.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(), 0));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v2/homepage",
        "/v2/homepage/about",
        "/v2/homepage/branding-color",
        "/v2/homepage/recruit",
        "/v2/recruit",
        "/v2/recruit/part",
        "/v2/projects"
      })
  @DisplayName("공식 홈페이지 조회 API는 인증 없이 조회할 수 있다")
  void allowsPublicPages(String path) throws Exception {
    mvc.perform(get(path).param("part", "WEB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    verifyNoInteractions(tokens);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/v2/admin", "/v2/private"})
  @DisplayName("공개 경로 외의 API는 인증 없이 접근할 수 없다")
  void requiresAuthentication(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isUnauthorized());
  }

  @org.junit.jupiter.api.Test
  @DisplayName("메인 페이지는 FAQ와 후기 유형 및 소식 게시일과 활동 통계를 반환한다")
  void mainPageIncludesDetails() throws Exception {
    var generation = new Generation(39, "SOPT", null, null, null, null);
    var faq =
        new org.sopt.makers.domain.official.faq.Faq(
            1L,
            org.sopt.makers.domain.official.faq.FaqCategory.QUALIFICATION,
            List.of(new org.sopt.makers.domain.official.faq.Faq.QuestionAnswer("질문", "답변")));
    var review =
        new org.sopt.makers.domain.official.homepage.review.HomepageReview(
            1L,
            org.sopt.makers.domain.official.homepage.review.HomepageReviewType.RECRUITING,
            "후기",
            "내용",
            "작성자",
            null,
            null);
    var news =
        new org.sopt.makers.domain.official.news.News(
            1,
            "image",
            "소식",
            "link",
            java.time.Instant.parse("2026-01-01T16:00:00Z"),
            false,
            null,
            null);
    when(homepage.getMainPage())
        .thenReturn(
            new HomepageFacade.MainPageData(
                generation,
                List.of(),
                List.of(),
                List.of(news),
                List.of(),
                new HomepageFacade.ActivitiesRecords(10, 2, 3, 4, 5, 19),
                List.of(review),
                List.of(faq)));
    mvc.perform(get("/v2/homepage"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.faq[0].category").value("지원자격"))
        .andExpect(jsonPath("$.data.faq[0].questions[0].question").value("질문"))
        .andExpect(jsonPath("$.data.reviews[0].type").value("RECRUITING"))
        .andExpect(jsonPath("$.data.latestNews[0].publishedDate").value("2026.01.02"))
        .andExpect(jsonPath("$.data.activitiesRecords.foundingTeamCount").value(4))
        .andExpect(jsonPath("$.data.activitiesRecords.availableServiceCount").value(5));
  }

  @org.junit.jupiter.api.Test
  @DisplayName("브랜딩 색상 조회는 최신 기수의 네 가지 색상을 반환한다")
  void brandingColors() throws Exception {
    when(homepage.getBrandingGeneration())
        .thenReturn(
            new Generation(
                39,
                "SOPT",
                null,
                null,
                null,
                new org.sopt.makers.domain.official.generation.BrandingColor(
                    "#111111", "#222222", "#333333", "#444444")));
    mvc.perform(get("/v2/homepage/branding-color"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.darkModeKeyColor").value("#111111"))
        .andExpect(jsonPath("$.data.darkModeTextColor").value("#222222"))
        .andExpect(jsonPath("$.data.lightModeKeyColor").value("#333333"))
        .andExpect(jsonPath("$.data.lightModeTextColor").value("#444444"));
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    HomepageController.class,
    RecruitPageController.class,
    ProjectController.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    HomepageFacade homepage() {
      return mock(HomepageFacade.class);
    }

    @Bean
    RecruitPageFacade recruit() {
      return mock(RecruitPageFacade.class);
    }

    @Bean
    ProjectService projects() {
      return mock(ProjectService.class);
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
