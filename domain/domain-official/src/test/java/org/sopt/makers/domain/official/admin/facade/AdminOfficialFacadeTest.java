package org.sopt.makers.domain.official.admin.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.domain.official.activityschedule.service.ActivityScheduleService;
import org.sopt.makers.domain.official.admin.exception.OfficialAdminException;
import org.sopt.makers.domain.official.admin.port.*;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.FaqCategory;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.BrandingColor;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.homepage.review.*;
import org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewException;
import org.sopt.makers.domain.official.homepage.review.port.HomepageReviewRepositoryPort;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.sopt.makers.domain.official.member.service.MemberService;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;
import org.sopt.makers.domain.official.recruitment.service.RecruitmentService;

class AdminOfficialFacadeTest {
  private final GenerationService generations = mock(GenerationService.class);
  private final CoreValueService coreValues = mock(CoreValueService.class);
  private final MemberService members = mock(MemberService.class);
  private final RecruitmentService recruitments = mock(RecruitmentService.class);
  private final ActivityScheduleService schedules = mock(ActivityScheduleService.class);
  private final PartService parts = mock(PartService.class);
  private final RecruitPartIntroductionService introductions =
      mock(RecruitPartIntroductionService.class);
  private final FaqService faqs = mock(FaqService.class);
  private final NewsService news = mock(NewsService.class);
  private final HomepageReviewService reviews = mock(HomepageReviewService.class);
  private final AdminFileStoragePort files = mock(AdminFileStoragePort.class);
  private final AdminCachePort cache = mock(AdminCachePort.class);
  private final AdminOfficialFacade facade = facade(reviews);

  @Test
  @DisplayName("공통 탭 저장은 DB에 반영하지 않고 캐시에 저장한다")
  void cachesCommon() {
    facade.addCommonData(
        new AdminOfficialFacade.AddCommonRequest(
            39, "SOPT", "#112233", "white", "#AABBCC", "black", List.of()));
    verify(cache)
        .putCommon(
            39,
            new AdminCachePort.CommonCacheData(
                39, "SOPT", "#112233", "white", "#AABBCC", "black", List.of()));
    verifyNoInteractions(generations, recruitments);
  }

  @Test
  @DisplayName("공통 탭 확정은 색상 접두사를 정리하고 빈 모집 일정도 반영한다")
  void confirmsCommon() {
    when(cache.getCommon(39))
        .thenReturn(
            new AdminCachePort.CommonCacheData(
                39, "SOPT", "#112233", "white", "#AABBCC", "black", List.of()));
    assertThat(facade.addCommonDataConfirm(39).message()).isEqualTo("공통 탭 배포 성공");
    verify(generations)
        .createOrUpdate(39, "SOPT", new BrandingColor("112233", "white", "AABBCC", "black"));
    verify(recruitments)
        .bulkCreate(new RecruitmentService.BulkCreateRecruitmentsCommand(39, List.of()));
    verify(cache).evictCommon(39);
  }

  @ParameterizedTest
  @ValueSource(strings = {"common", "home", "about", "recruit"})
  @DisplayName("임시 저장 내역이 없으면 확정할 수 없다")
  void rejectsMissingCache(String tab) {
    assertThatThrownBy(
            () -> {
              switch (tab) {
                case "common" -> facade.addCommonDataConfirm(39);
                case "home" -> facade.addHomeDataConfirm(39);
                case "about" -> facade.addAboutDataConfirm(39);
                default -> facade.addRecruitDataConfirm(39);
              }
            })
        .isInstanceOf(OfficialAdminException.class);
    verifyNoInteractions(generations, news, reviews, faqs);
  }

  @Test
  @DisplayName("홈 탭 저장은 후기 유형을 캐시에 유지한다")
  void cachesReviewTypes() {
    var review =
        new AdminOfficialFacade.AddHomeRequest.ReviewRequest(
            "후기", "내용", "작성자", HomepageReviewType.RECRUITING);
    facade.addHomeData(new AdminOfficialFacade.AddHomeRequest(39, null, null, List.of(review)));
    verify(cache)
        .putHome(
            39,
            new AdminCachePort.HomeCacheData(
                39,
                null,
                null,
                List.of(
                    new AdminCachePort.ReviewData(
                        "후기", "내용", "작성자", HomepageReviewType.RECRUITING))));
    verify(reviews).validateMaxCountPerType(List.of(HomepageReviewType.RECRUITING));
    verifyNoInteractions(generations, files);
  }

  @Test
  @DisplayName("후기가 유형별 최대 개수를 넘으면 캐시를 저장하지 않는다")
  void rejectsTooManyReviewsBeforeCaching() {
    var review = new AdminOfficialFacade.AddHomeRequest.ReviewRequest("후기", "내용", "작성자", null);
    var entries = java.util.Collections.nCopies(8, review);
    var realReviews = new HomepageReviewService(mock(HomepageReviewRepositoryPort.class));
    assertThatThrownBy(
            () ->
                facade(realReviews)
                    .addHomeData(
                        new AdminOfficialFacade.AddHomeRequest(39, "home.png", null, entries)))
        .isInstanceOf(HomepageReviewException.class);
    verifyNoInteractions(cache, files);
  }

  @Test
  @DisplayName("홈 탭 확정은 후기 유형을 유지하고 생략된 이미지와 소식은 변경하지 않는다")
  void confirmsReviewTypes() {
    when(cache.getHome(39))
        .thenReturn(
            new AdminCachePort.HomeCacheData(
                39,
                null,
                null,
                List.of(
                    new AdminCachePort.ReviewData(
                        "후기", "내용", "작성자", HomepageReviewType.RECRUITING))));
    facade.addHomeDataConfirm(39);
    verify(reviews)
        .replaceAll(
            new HomepageReviewService.BulkCreateHomepageReviewsCommand(
                List.of(
                    new HomepageReviewService.BulkCreateHomepageReviewsCommand.ReviewData(
                        "후기", "내용", "작성자", HomepageReviewType.RECRUITING))));
    verifyNoInteractions(generations, news);
    verify(cache).evictHome(39);
  }

  @Test
  @DisplayName("홈 탭에서 빈 후기 배열은 전체 삭제 요청으로 전달한다")
  void clearsReviews() {
    when(cache.getHome(39)).thenReturn(new AdminCachePort.HomeCacheData(39, null, null, List.of()));
    facade.addHomeDataConfirm(39);
    verify(reviews)
        .replaceAll(new HomepageReviewService.BulkCreateHomepageReviewsCommand(List.of()));
  }

  @Test
  @DisplayName("홈 탭에서 후기를 생략하면 기존 후기를 유지한다")
  void preservesOmittedReviews() {
    when(cache.getHome(39)).thenReturn(new AdminCachePort.HomeCacheData(39, null, null, null));
    facade.addHomeDataConfirm(39);
    verifyNoInteractions(reviews);
  }

  @Test
  @DisplayName("홈 탭 확정은 업로드 주소를 원본 이미지 주소로 바꿔 저장한다")
  void confirmsImageUploads() {
    when(cache.getHome(39))
        .thenReturn(
            new AdminCachePort.HomeCacheData(
                39,
                "header?signature",
                List.of(new AdminCachePort.NewsData("소식", "link", "news?signature")),
                null));
    when(files.getOriginalUrl("header?signature")).thenReturn("header");
    when(files.getOriginalUrl("news?signature")).thenReturn("news");
    facade.addHomeDataConfirm(39);
    verify(generations).updateHomeHeaderImage(39, "header");
    verify(news)
        .replaceAll(
            new NewsService.BulkCreateNewsCommand(
                List.of(new NewsService.BulkCreateNewsCommand.NewsData("소식", "link", "news"))));
  }

  @Test
  @DisplayName("모집 탭 저장은 FAQ를 캐시에 유지한다")
  void cachesFaqs() {
    var data =
        new AdminCachePort.FaqData(
            FaqCategory.QUALIFICATION, List.of(new AdminCachePort.QuestionData("질문", "답변")));
    facade.addRecruitData(
        new AdminOfficialFacade.AddRecruitRequest(39, null, null, null, null, List.of(data)));
    verify(cache)
        .putRecruit(
            39, new AdminCachePort.RecruitCacheData(39, null, null, null, null, List.of(data)));
    verifyNoInteractions(faqs);
  }

  @Test
  @DisplayName("모집 탭 저장은 중복 FAQ 카테고리를 거부한다")
  void rejectsDuplicateCategories() {
    var data = new AdminCachePort.FaqData(FaqCategory.QUALIFICATION, List.of());
    assertThatThrownBy(
            () ->
                facade.addRecruitData(
                    new AdminOfficialFacade.AddRecruitRequest(
                        39, "header.png", null, null, null, List.of(data, data))))
        .isInstanceOf(OfficialAdminException.class);
    verifyNoInteractions(cache, files);
  }

  @Test
  @DisplayName("모집 탭 확정은 전달받은 FAQ 목록으로 전체를 교체한다")
  void confirmsFaqs() {
    when(cache.getRecruit(39))
        .thenReturn(
            new AdminCachePort.RecruitCacheData(
                39,
                null,
                null,
                null,
                null,
                List.of(
                    new AdminCachePort.FaqData(
                        FaqCategory.QUALIFICATION,
                        List.of(new AdminCachePort.QuestionData("질문", "답변"))))));
    facade.addRecruitDataConfirm(39);
    verify(faqs)
        .bulkCreate(
            List.of(
                new FaqService.BulkCreateFaqData(
                    FaqCategory.QUALIFICATION,
                    List.of(new FaqService.BulkCreateFaqData.QuestionData("질문", "답변")))));
    verify(cache).evictRecruit(39);
  }

  @Test
  @DisplayName("빈 FAQ 목록을 확정하면 전체 삭제 요청으로 전달한다")
  void clearsFaqs() {
    when(cache.getRecruit(39))
        .thenReturn(new AdminCachePort.RecruitCacheData(39, null, null, null, null, List.of()));
    facade.addRecruitDataConfirm(39);
    verify(faqs).bulkCreate(List.of());
  }

  @Test
  @DisplayName("모집 탭에서 FAQ를 생략하면 기존 FAQ를 유지한다")
  void preservesOmittedFaqs() {
    when(cache.getRecruit(39))
        .thenReturn(new AdminCachePort.RecruitCacheData(39, null, null, null, null, null));
    facade.addRecruitDataConfirm(39);
    verifyNoInteractions(faqs);
  }

  private AdminOfficialFacade facade(HomepageReviewService reviewService) {
    return new AdminOfficialFacade(
        generations,
        coreValues,
        members,
        recruitments,
        schedules,
        parts,
        introductions,
        faqs,
        news,
        reviewService,
        files,
        cache);
  }
}
