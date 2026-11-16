package org.sopt.makers.api.controller.admin.official;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.common.config.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.*;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.official.admin.facade.AdminOfficialFacade;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.sopt.makers.domain.official.faq.*;
import org.sopt.makers.domain.official.generation.*;
import org.sopt.makers.domain.official.homepage.review.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(AdminOfficialApiTest.Config.class)
@ActiveProfiles("prod")
class AdminOfficialApiTest {
  private static final String BASE = "/v2/admin";
  private static final String COMMON =
      """
   {"generation":39,"name":"SOPT","brandingColor":{"darkModeKeyColor":"#112233","darkModeTextColor":"white","lightModeKeyColor":"AABBCC","lightModeTextColor":"black"},"recruitSchedule":[]}
   """;
  @Autowired private WebApplicationContext context;
  @Autowired private AdminOfficialFacade facade;
  @Autowired private JwtAccessTokenService tokens;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(facade, tokens);
    when(tokens.parse("Bearer admin"))
        .thenAnswer(
            inv -> new CustomAuthentication("1", "", List.of(new SimpleGrantedAuthority("ADMIN"))));
    when(tokens.parse("Bearer member"))
        .thenAnswer(
            inv ->
                new CustomAuthentication("2", "", List.of(new SimpleGrantedAuthority("MEMBER"))));
    when(facade.addCommonData(any())).thenReturn(new AdminOfficialFacade.CommonResult(39));
    when(facade.addHomeData(any()))
        .thenReturn(
            new AdminOfficialFacade.HomeResult(
                39,
                "https://home/upload",
                List.of(new AdminCachePort.NewsData("소식", "https://news", "https://news/upload"))));
    when(facade.addAboutData(any()))
        .thenReturn(
            new AdminOfficialFacade.AboutResult(39, "https://about/upload", List.of(), List.of()));
    when(facade.addRecruitData(any()))
        .thenReturn(new AdminOfficialFacade.RecruitResult(39, "https://recruit/upload"));
    when(facade.addCommonDataConfirm(39))
        .thenReturn(new AdminOfficialFacade.ConfirmResult("공통 탭 배포 성공"));
    when(facade.addHomeDataConfirm(39))
        .thenReturn(new AdminOfficialFacade.ConfirmResult("홈 탭 배포 성공"));
    when(facade.addAboutDataConfirm(39))
        .thenReturn(new AdminOfficialFacade.ConfirmResult("소개 탭 배포 성공"));
    when(facade.addRecruitDataConfirm(39))
        .thenReturn(new AdminOfficialFacade.ConfirmResult("모집안내 탭 배포 성공"));
    when(facade.getMain(39)).thenReturn(mainData());
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "common",
        "home",
        "about",
        "recruit",
        "common/confirm",
        "home/confirm",
        "about/confirm",
        "recruit/confirm"
      })
  @DisplayName("관리자는 탭별 저장 및 확정 API를 호출할 수 있다")
  void allowsAdminPost(String path) throws Exception {
    mvc.perform(adminPost(path, path.equals("common") ? COMMON : "{\"generation\":39}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "common",
        "home",
        "about",
        "recruit",
        "common/confirm",
        "home/confirm",
        "about/confirm",
        "recruit/confirm"
      })
  @DisplayName("홈페이지 관리 API는 JWT 없이 호출할 수 없다")
  void rejectsAnonymous(String path) throws Exception {
    var request =
        path.isEmpty()
            ? get(BASE).param("generation", "39")
            : post(BASE + "/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"generation\":39}");
    mvc.perform(request).andExpect(status().isUnauthorized());
    verifyNoInteractions(facade);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "common",
        "home",
        "about",
        "recruit",
        "common/confirm",
        "home/confirm",
        "about/confirm",
        "recruit/confirm"
      })
  @DisplayName("일반 회원은 홈페이지 관리 API를 호출할 수 없다")
  void rejectsMember(String path) throws Exception {
    var request =
        path.isEmpty()
            ? get(BASE).param("generation", "39")
            : post(BASE + "/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"generation\":39}");
    mvc.perform(request.header("Authorization", "Bearer member")).andExpect(status().isForbidden());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("공통 탭의 중첩 브랜딩 컬러를 내부 명령으로 변환한다")
  void bindsBrandingColor() throws Exception {
    mvc.perform(adminPost("common", COMMON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.generation").value(39));
    verify(facade)
        .addCommonData(
            new AdminOfficialFacade.AddCommonRequest(
                39, "SOPT", "#112233", "white", "AABBCC", "black", List.of()));
  }

  @Test
  @DisplayName("홈 탭은 후기 유형을 전달하고 이미지 업로드 URL을 반환한다")
  void bindsHomeReviews() throws Exception {
    mvc.perform(
            adminPost(
                "home",
                """
      {"generation":39,"homeHeaderImageFileName":"home.png","news":[{"title":"소식","link":"https://news","imageFileName":"news.png"}],"review":[{"title":"후기","content":"내용","authorInfo":"작성자","type":"RECRUITING"}]}
      """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.homeHeaderImage").value("https://home/upload"))
        .andExpect(jsonPath("$.data.news[0].imagePresignedUrl").value("https://news/upload"));
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddHomeRequest.class);
    verify(facade).addHomeData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().reviews().getFirst().type())
        .isEqualTo(HomepageReviewType.RECRUITING);
  }

  @Test
  @DisplayName("소개 탭은 중첩 SNS와 날짜를 내부 명령으로 전달한다")
  void bindsSnsAndDates() throws Exception {
    mvc.perform(
            adminPost(
                "about",
                """
      {"generation":39,"member":[{"role":"회장","name":"홍길동","affiliation":"SOPT","introduction":"소개","sns":{"email":"user@sopt.org","github":"https://github.com/user"}}],"activitySchedule":[{"name":"OT","startDate":"2026-03-28","endDate":null}]}
      """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.headerImage").value("https://about/upload"));
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddAboutRequest.class);
    verify(facade).addAboutData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().members().getFirst().snsEmail())
        .isEqualTo("user@sopt.org");
    org.assertj.core.api.Assertions.assertThat(captor.getValue().members().getFirst().snsGithub())
        .isEqualTo("https://github.com/user");
    org.assertj.core.api.Assertions.assertThat(
            captor.getValue().activitySchedules().getFirst().startDate())
        .isEqualTo(LocalDate.of(2026, 3, 28));
  }

  @Test
  @DisplayName("카테고리 FAQ가 있으면 레거시 파트 FAQ보다 우선한다")
  void bindsFaqs() throws Exception {
    mvc.perform(
            adminPost(
                "recruit",
                """
      {"generation":39,"recruitQuestion":[{"part":"서버","questions":[{"question":"파트 질문","answer":"답변"}]}],"faq":[{"category":" 지원자격 ","questions":[{"question":"지원 질문","answer":"답변"}]}]}
      """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.recruitHeaderImage").value("https://recruit/upload"));
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddRecruitRequest.class);
    verify(facade).addRecruitData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().faqs())
        .extracting(AdminCachePort.FaqData::category)
        .containsExactly(FaqCategory.QUALIFICATION);
  }

  @Test
  @DisplayName("관리자 전체 조회는 후기 유형과 카테고리 FAQ를 포함한다")
  void getsMainData() throws Exception {
    mvc.perform(get(BASE).param("generation", "39").header("Authorization", "Bearer admin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.generation").value(39))
        .andExpect(jsonPath("$.data.brandingColor.darkModeKeyColor").value("112233"))
        .andExpect(jsonPath("$.data.review[0].type").value("RECRUITING"))
        .andExpect(jsonPath("$.data.faq[0].category").value("지원자격"))
        .andExpect(jsonPath("$.data.faq[0].questions[0].question").value("질문"))
        .andExpect(jsonPath("$.data.recruitQuestion[0].part").value("서버"));
    verify(facade).getMain(39);
  }

  @ParameterizedTest
  @ValueSource(strings = {"common/confirm", "home/confirm", "about/confirm", "recruit/confirm"})
  @DisplayName("확정 요청에는 양수인 기수가 필요하다")
  void validatesConfirm(String path) throws Exception {
    mvc.perform(adminPost(path, "{}")).andExpect(status().isBadRequest());
    mvc.perform(adminPost(path, "{\"generation\":0}")).andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"generation\":39,\"name\":\"SOPT\"}",
        "{\"generation\":39,\"name\":\"SOPT\",\"brandingColor\":{\"darkModeKeyColor\":\"invalid\",\"darkModeTextColor\":\"white\",\"lightModeKeyColor\":\"AABBCC\",\"lightModeTextColor\":\"black\"}}"
      })
  @DisplayName("공통 탭은 브랜딩 컬러의 필수 값과 형식을 검증한다")
  void validatesBrandingColor(String body) throws Exception {
    mvc.perform(adminPost("common", body)).andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("후기 길이와 중첩 FAQ의 내용을 검증한다")
  void validatesNestedRequests() throws Exception {
    mvc.perform(
            adminPost(
                "home",
                """
     {"generation":39,"review":[{"title":"12345678901","content":"내용","authorInfo":"작성자"}]}
     """))
        .andExpect(status().isBadRequest());
    mvc.perform(
            adminPost(
                "recruit",
                """
     {"generation":39,"faq":[{"category":"잘못된 값","questions":[{"question":"질문","answer":"답변"}]}]}
     """))
        .andExpect(status().isBadRequest());
    mvc.perform(
            adminPost(
                "recruit",
                """
     {"generation":39,"faq":[{"category":"지원자격","questions":[null]}]}
     """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("목록을 생략하면 null이고 빈 배열을 보내면 빈 목록으로 전달한다")
  void preservesNullAndEmptyLists() throws Exception {
    mvc.perform(adminPost("home", "{\"generation\":39}")).andExpect(status().isCreated());
    mvc.perform(adminPost("home", "{\"generation\":39,\"review\":[],\"news\":[]}"))
        .andExpect(status().isCreated());
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddHomeRequest.class);
    verify(facade, times(2)).addHomeData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getAllValues().get(0).reviews()).isNull();
    org.assertj.core.api.Assertions.assertThat(captor.getAllValues().get(1).reviews()).isEmpty();
  }

  @Test
  @DisplayName("관리자 조회에는 기수가 필요하다")
  void requiresGeneration() throws Exception {
    mvc.perform(get(BASE).header("Authorization", "Bearer admin"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("레거시 파트 FAQ 요청은 API에서 공통 FAQ 데이터로 변환한다")
  void convertsPartFaqRequest() throws Exception {
    mvc.perform(
            adminPost(
                "recruit",
                """
      {"generation":39,"recruitQuestion":[{"part":"서버","questions":[{"question":"질문","answer":"답변"}]}]}
      """))
        .andExpect(status().isCreated());
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddRecruitRequest.class);
    verify(facade).addRecruitData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().faqs())
        .extracting(AdminCachePort.FaqData::category)
        .containsExactly(FaqCategory.SERVER);
  }

  @Test
  @DisplayName("카테고리 FAQ가 빈 배열이면 파트 FAQ가 있어도 전체 삭제를 요청한다")
  void prioritizesEmptyCategoryFaqs() throws Exception {
    mvc.perform(
            adminPost(
                "recruit",
                """
      {"generation":39,"faq":[],"recruitQuestion":[{"part":"서버","questions":[{"question":"질문","answer":"답변"}]}]}
      """))
        .andExpect(status().isCreated());
    var captor = ArgumentCaptor.forClass(AdminOfficialFacade.AddRecruitRequest.class);
    verify(facade).addRecruitData(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().faqs()).isEmpty();
  }

  private MockHttpServletRequestBuilder adminPost(String path, String body) {
    return post(BASE + "/" + path)
        .header("Authorization", "Bearer admin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body);
  }

  private AdminOfficialFacade.AdminMainData mainData() {
    return new AdminOfficialFacade.AdminMainData(
        new Generation(
            39, "SOPT", null, null, null, new BrandingColor("112233", "white", "AABBCC", "black")),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(new Faq(1L, FaqCategory.SERVER, List.of())),
        List.of(),
        List.of(
            new HomepageReview(1L, HomepageReviewType.RECRUITING, "후기", "내용", "작성자", null, null)),
        List.of(
            new Faq(2L, FaqCategory.QUALIFICATION, List.of(new Faq.QuestionAnswer("질문", "답변")))));
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    AdminOfficialController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    AdminOfficialFacade facade() {
      return mock(AdminOfficialFacade.class);
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
