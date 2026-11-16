package org.sopt.makers.domain.official.homepage.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.activityschedule.service.ActivityScheduleService;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.homepage.port.*;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.sopt.makers.domain.official.member.service.MemberService;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;
import org.sopt.makers.domain.official.recruitment.service.RecruitmentService;

class HomepageFacadeTest {
  private final GenerationService generations = mock(GenerationService.class);
  private final ProjectClientPort projects = mock(ProjectClientPort.class);
  private final HomepageUserCountPort users = mock(HomepageUserCountPort.class);
  private final HomepageStudyCountPort studies = mock(HomepageStudyCountPort.class);
  private final NewsService news = mock(NewsService.class);
  private final FaqService faqs = mock(FaqService.class);
  private final HomepageReviewService reviews = mock(HomepageReviewService.class);
  private final HomepageFacade facade =
      new HomepageFacade(
          generations,
          mock(PartService.class),
          news,
          mock(RecruitmentService.class),
          reviews,
          mock(CoreValueService.class),
          mock(MemberService.class),
          mock(ActivityScheduleService.class),
          mock(RecruitPartIntroductionService.class),
          faqs,
          projects,
          users,
          studies);

  @Test
  @DisplayName("활동 통계는 이전 기수와 전체 기수의 프로젝트 수를 구분한다")
  void aggregatesActivityRecords() {
    when(generations.findLatest()).thenReturn(new Generation(39, "SOPT", null, null, null, null));
    when(users.getUserCountByGeneration(38)).thenReturn(100);
    when(studies.getStudyCountByGeneration(38)).thenReturn(5);
    when(projects.fetchAll())
        .thenReturn(
            List.of(project(38, true, false), project(37, false, true), project(38, true, true)));
    var result = facade.getMainPage();
    assertThat(result.activitiesRecords().activitiesUserCount()).isEqualTo(100);
    assertThat(result.activitiesRecords().projectCounts()).isEqualTo(2);
    assertThat(result.activitiesRecords().studyCounts()).isEqualTo(5);
    assertThat(result.activitiesRecords().foundingTeamCount()).isEqualTo(2);
    assertThat(result.activitiesRecords().availableServiceCount()).isEqualTo(2);
    verify(news).findLatestForHomepage();
    verify(faqs).findCategoryFaqs();
    verify(reviews).findAll();
  }

  @Test
  @DisplayName("활동 통계 조회가 실패해도 메인 페이지는 기본 통계로 반환한다")
  void returnsDefaultStatisticsOnFailure() {
    when(generations.findLatest()).thenReturn(new Generation(39, "SOPT", null, null, null, null));
    when(users.getUserCountByGeneration(38)).thenThrow(new IllegalStateException("조회 실패"));
    assertThat(facade.getMainPage().activitiesRecords())
        .isEqualTo(new HomepageFacade.ActivitiesRecords(0, 0, 0, 0, 0, 0));
  }

  private Project project(int generation, boolean founding, boolean available) {
    return new Project(
        1L,
        "프로젝트",
        generation,
        null,
        List.of(),
        null,
        null,
        null,
        null,
        founding,
        available,
        List.of());
  }
}
