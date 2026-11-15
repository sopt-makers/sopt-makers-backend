package org.sopt.makers.api.controller.admin.banner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.admin.banner.service.BannerService;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminBannerControllerTest {

  private static final String BASE = "/api/v1/banners";
  private static final BannerService.BannerDetail DETAIL =
      new BannerService.BannerDetail(
          1L,
          "published",
          "cr_main",
          "product",
          "SOPT",
          "https://sopt.org",
          LocalDate.of(2026, 11, 1),
          LocalDate.of(2026, 11, 30),
          "pc.png",
          "mobile.png");

  private final BannerService service = mock(BannerService.class);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    AdminBannerController controller = new AdminBannerController(service);
    ReflectionTestUtils.setField(controller, "officialApiKey", "test-official-key");
    mvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("페이징 조건을 생략하면 기본 조건으로 배너 목록을 조회한다")
  void getsBanners() throws Exception {
    when(service.getBannersWithPagination(
            BannerService.FilterCriteria.ALL, BannerService.SortCriteria.STATUS, 1, 10))
        .thenReturn(new BannerService.BannerPage<>(List.of(), 0, 10, 1));

    mvc.perform(get(BASE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.currentPage").value(1))
        .andExpect(jsonPath("$.data.limit").value(10));
    verify(service)
        .getBannersWithPagination(
            BannerService.FilterCriteria.ALL, BannerService.SortCriteria.STATUS, 1, 10);
  }

  @Test
  @DisplayName("배너 ID로 상세 정보를 조회한다")
  void getsBannerDetail() throws Exception {
    when(service.getBannerDetail(1L)).thenReturn(DETAIL);
    mvc.perform(get(BASE + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1));
    verify(service).getBannerDetail(1L);
  }

  @Test
  @DisplayName("배너 정보와 이미지를 전달하면 배너를 생성하고 201을 반환한다")
  void createsBanner() throws Exception {
    when(service.createBanner(any())).thenReturn(DETAIL);
    mvc.perform(bannerMultipart(HttpMethod.POST, BASE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(1));
    verify(service)
        .createBanner(
            argThat(
                command ->
                    command.location().equals("cr_main")
                        && command.contentType().equals("product")
                        && command.publisher().equals("SOPT")));
  }

  @Test
  @DisplayName("배너 ID와 수정 정보를 전달하면 배너를 수정한다")
  void updatesBanner() throws Exception {
    when(service.updateBanner(eq(1L), any())).thenReturn(DETAIL);
    mvc.perform(bannerMultipart(HttpMethod.PUT, BASE + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1));
    verify(service)
        .updateBanner(
            eq(1L),
            argThat(
                command ->
                    command.location().equals("cr_main")
                        && command.publisher().equals("SOPT")
                        && command.contentType().equals("product")));
  }

  @Test
  @DisplayName("배너 ID로 배너를 삭제한다")
  void deletesBanner() throws Exception {
    mvc.perform(delete(BASE + "/1")).andExpect(status().isOk());
    verify(service).deleteBanner(1L);
  }

  @Test
  @DisplayName("배너 위치에 해당하는 외부 이미지 정보를 반환한다")
  void getsExternalImages() throws Exception {
    when(service.getExternalBanners("cr_main"))
        .thenReturn(
            List.of(
                new BannerService.BannerImageWithBothPlatforms(
                    "pc.png",
                    "mobile.png",
                    LocalDate.of(2026, 11, 1),
                    LocalDate.of(2026, 11, 30),
                    "https://sopt.org")));
    mvc.perform(
            get(BASE + "/images")
                .param("location", "cr_main")
                .header("api-key", "test-official-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].pc_url").value("pc.png"))
        .andExpect(jsonPath("$.data[0].mobile_url").value("mobile.png"))
        .andExpect(jsonPath("$.data[0].link").value("https://sopt.org"));
    verify(service).getExternalBanners("cr_main");
  }

  private static MockMultipartHttpServletRequestBuilder bannerMultipart(
      HttpMethod method, String path) {
    var request = multipart(method, path);
    Map.of(
            "location",
            "cr_main",
            "content_type",
            "product",
            "publisher",
            "SOPT",
            "start_date",
            "2026-11-01",
            "end_date",
            "2026-11-30")
        .forEach(
            (key, value) ->
                request.file(
                    new MockMultipartFile(
                        key,
                        "",
                        "text/plain",
                        value.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
    request.file(new MockMultipartFile("image_pc", "pc.png", "image/png", new byte[] {1}));
    request.file(new MockMultipartFile("image_mobile", "mobile.png", "image/png", new byte[] {2}));
    return request;
  }
}
