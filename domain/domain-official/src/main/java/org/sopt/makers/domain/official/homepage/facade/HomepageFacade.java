package org.sopt.makers.domain.official.homepage.facade;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.activityschedule.service.ActivityScheduleService;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.homepage.port.HomepageStudyCountPort;
import org.sopt.makers.domain.official.homepage.port.HomepageUserCountPort;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.service.MemberService;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;
import org.sopt.makers.domain.official.recruitment.Recruitment;
import org.sopt.makers.domain.official.recruitment.service.RecruitmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomepageFacade {

  private static final int ORGANIZATION_YEAR = 2008;

  private final GenerationService generationService;
  private final PartService partService;
  private final NewsService newsService;
  private final RecruitmentService recruitmentService;
  private final HomepageReviewService homepageReviewService;
  private final CoreValueService coreValueService;
  private final MemberService memberService;
  private final ActivityScheduleService activityScheduleService;
  private final RecruitPartIntroductionService recruitPartIntroductionService;
  private final FaqService faqService;
  private final ProjectClientPort projectClientPort;
  private final HomepageUserCountPort userCountPort;
  private final HomepageStudyCountPort studyCountPort;

  public MainPageData getMainPage() {
    Generation generation = generationService.findLatest();
    Integer generationId = generation.id();

    List<RecruitPartInfo> partInfos = partService.findByGeneration(generationId);
    List<CoreValue> coreValues = coreValueService.findByGeneration(generationId);
    List<News> news = newsService.findAll();
    List<Recruitment> recruitments = recruitmentService.findByGeneration(generationId);
    ActivitiesRecords activitiesRecords = fetchActivitiesRecords(generationId - 1);
    List<HomepageReview> reviews = homepageReviewService.getReviews();

    return new MainPageData(
        generation, coreValues, partInfos, news, recruitments, activitiesRecords, reviews);
  }

  public AboutPageData getAboutPage() {
    Generation generation = generationService.findLatest();
    Integer generationId = generation.id();

    List<CoreValue> coreValues = coreValueService.findByGeneration(generationId);
    List<RecruitPartInfo> partInfos = partService.findByGeneration(generationId);
    List<Member> members = memberService.findByGeneration(generationId);
    List<ActivitySchedule> activitySchedules =
        activityScheduleService.findByGeneration(generationId);

    return new AboutPageData(generation, coreValues, partInfos, members, activitySchedules);
  }

  public RecruitPageData getRecruitPage() {
    Generation generation = generationService.findLatest();
    Integer generationId = generation.id();

    List<Recruitment> recruitments = recruitmentService.findByGeneration(generationId);
    List<RecruitPartIntroduction> recruitPartIntros =
        recruitPartIntroductionService.findByGeneration(generationId);
    List<Faq> faqs = faqService.findAll();

    return new RecruitPageData(generation, recruitments, recruitPartIntros, faqs);
  }

  private ActivitiesRecords fetchActivitiesRecords(Integer generationId) {
    try {
      int userCount = userCountPort.getUserCountByGeneration(generationId);

      List<Project> allProjects = projectClientPort.fetchAll();
      int projectCount =
          (int)
              allProjects.stream()
                  .filter(p -> p.generation() != null && p.generation().equals(generationId))
                  .count();

      int studyCount = studyCountPort.getStudyCountByGeneration(generationId);

      int operationPeriod = LocalDate.now().getYear() - ORGANIZATION_YEAR + 1;

      return new ActivitiesRecords(userCount, projectCount, studyCount, operationPeriod);
    } catch (Exception e) {
      log.warn("activities records 조회 실패 generationId={}: {}", generationId, e.getMessage());
      return new ActivitiesRecords(0, 0, 0, 0);
    }
  }

  public record ActivitiesRecords(
      int activitiesUserCount, int projectCounts, int studyCounts, int operationPeriod) {}

  public record MainPageData(
      Generation generation,
      List<CoreValue> coreValues,
      List<RecruitPartInfo> partInfos,
      List<News> news,
      List<Recruitment> recruitments,
      ActivitiesRecords activitiesRecords,
      List<HomepageReview> reviews) {}

  public record AboutPageData(
      Generation generation,
      List<CoreValue> coreValues,
      List<RecruitPartInfo> partInfos,
      List<Member> members,
      List<ActivitySchedule> activitySchedules) {}

  public record RecruitPageData(
      Generation generation,
      List<Recruitment> recruitments,
      List<RecruitPartIntroduction> recruitPartIntros,
      List<Faq> faqs) {}
}
