package org.sopt.makers.domain.official.admin.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.activityschedule.service.ActivityScheduleService;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.sopt.makers.domain.official.admin.port.AdminFileStoragePort;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.BrandingColor;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.member.SnsLinks;
import org.sopt.makers.domain.official.member.service.MemberService;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;
import org.sopt.makers.domain.official.recruitment.Recruitment;
import org.sopt.makers.domain.official.recruitment.service.RecruitmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOfficialFacade {

  private final GenerationService generationService;
  private final CoreValueService coreValueService;
  private final MemberService memberService;
  private final RecruitmentService recruitmentService;
  private final ActivityScheduleService activityScheduleService;
  private final PartService partService;
  private final RecruitPartIntroductionService recruitPartIntroductionService;
  private final FaqService faqService;
  private final NewsService newsService;
  private final HomepageReviewService homepageReviewService;
  private final AdminFileStoragePort adminFileStoragePort;
  private final AdminCachePort adminCachePort;

  // ============================================================
  // GET: 전체 조회
  // ============================================================

  public AdminMainData getMain(Integer generationId) {
    Generation generation = generationService.findById(generationId);
    return new AdminMainData(
        generation,
        coreValueService.findByGeneration(generationId),
        memberService.findByGeneration(generationId),
        recruitmentService.findByGeneration(generationId),
        activityScheduleService.findByGeneration(generationId),
        partService.findByGeneration(generationId),
        recruitPartIntroductionService.findByGeneration(generationId),
        faqService.findAll(),
        newsService.findAll(),
        homepageReviewService.getReviews());
  }

  // ============================================================
  // 공통 탭
  // ============================================================

  @Transactional
  public CommonResult addCommonData(AddCommonRequest request) {
    AdminCachePort.CommonCacheData cacheData =
        new AdminCachePort.CommonCacheData(
            request.generationId(),
            request.name(),
            request.darkModeKeyColor(),
            request.darkModeTextColor(),
            request.lightModeKeyColor(),
            request.lightModeTextColor(),
            request.recruitSchedules());
    adminCachePort.putCommon(request.generationId(), cacheData);
    return new CommonResult(request.generationId());
  }

  @Transactional
  public ConfirmResult addCommonDataConfirm(Integer generationId) {
    AdminCachePort.CommonCacheData cached = adminCachePort.getCommon(generationId);
    if (cached == null) {
      throw new IllegalStateException("공통 탭 캐시 데이터 없음. generation=" + generationId);
    }

    BrandingColor brandingColor =
        new BrandingColor(
            stripHash(cached.darkModeKeyColor()),
            cached.darkModeTextColor(),
            stripHash(cached.lightModeKeyColor()),
            cached.lightModeTextColor());

    generationService.createOrUpdate(generationId, cached.name(), brandingColor);

    if (cached.recruitSchedules() != null && !cached.recruitSchedules().isEmpty()) {
      recruitmentService.bulkCreate(
          new RecruitmentService.BulkCreateRecruitmentsCommand(
              generationId,
              cached.recruitSchedules().stream()
                  .map(
                      rs ->
                          new RecruitmentService.BulkCreateRecruitmentsCommand.RecruitmentData(
                              rs.type(),
                              new RecruitmentService.BulkCreateRecruitmentsCommand.ScheduleData(
                                  rs.schedule().applicationStartTime(),
                                  rs.schedule().applicationEndTime(),
                                  rs.schedule().applicationResultTime(),
                                  rs.schedule().interviewStartTime(),
                                  rs.schedule().interviewEndTime(),
                                  rs.schedule().finalResultTime())))
                  .toList()));
    }

    adminCachePort.evictCommon(generationId);
    return new ConfirmResult("공통 탭 배포 성공");
  }

  // ============================================================
  // 홈 탭
  // ============================================================

  @Transactional
  public HomeResult addHomeData(AddHomeRequest request) {
    String homeHeaderImageUrl = null;
    if (hasText(request.homeHeaderImageFileName())) {
      homeHeaderImageUrl =
          adminFileStoragePort.generatePresignedUrl(
              request.homeHeaderImageFileName(), request.generationId() + "/");
    }

    List<AdminCachePort.NewsData> newsDataList = null;
    if (request.news() != null) {
      newsDataList =
          request.news().stream()
              .map(
                  n ->
                      new AdminCachePort.NewsData(
                          n.title(),
                          n.link(),
                          adminFileStoragePort.generatePresignedUrl(
                              n.imageFileName(), request.generationId() + "/news/")))
              .toList();
    }

    List<AdminCachePort.ReviewData> reviewDataList = null;
    if (request.reviews() != null) {
      reviewDataList =
          request.reviews().stream()
              .map(r -> new AdminCachePort.ReviewData(r.title(), r.content(), r.authorInfo()))
              .toList();
    }

    AdminCachePort.HomeCacheData cacheData =
        new AdminCachePort.HomeCacheData(
            request.generationId(), homeHeaderImageUrl, newsDataList, reviewDataList);
    adminCachePort.putHome(request.generationId(), cacheData);

    return new HomeResult(request.generationId(), homeHeaderImageUrl, newsDataList);
  }

  @Transactional
  public ConfirmResult addHomeDataConfirm(Integer generationId) {
    AdminCachePort.HomeCacheData cached = adminCachePort.getHome(generationId);
    if (cached == null) {
      throw new IllegalStateException("홈 탭 캐시 데이터 없음. generation=" + generationId);
    }

    if (cached.homeHeaderImageUrl() != null) {
      generationService.updateHomeHeaderImage(
          generationId, adminFileStoragePort.getOriginalUrl(cached.homeHeaderImageUrl()));
    }

    if (cached.news() != null) {
      newsService.replaceAll(
          new NewsService.BulkCreateNewsCommand(
              cached.news().stream()
                  .map(
                      n ->
                          new NewsService.BulkCreateNewsCommand.NewsData(
                              n.title(),
                              n.link(),
                              adminFileStoragePort.getOriginalUrl(n.imageUrl())))
                  .toList()));
    }

    if (cached.reviews() != null) {
      homepageReviewService.replaceAll(
          new HomepageReviewService.BulkCreateHomepageReviewsCommand(
              cached.reviews().stream()
                  .map(r -> new HomepageReviewService.BulkCreateHomepageReviewsCommand.ReviewData(
                      r.title(), r.content(), r.authorInfo()))
                  .toList()));
    }

    adminCachePort.evictHome(generationId);
    return new ConfirmResult("홈 탭 배포 성공");
  }

  // ============================================================
  // 소개 탭
  // ============================================================

  @Transactional
  public AboutResult addAboutData(AddAboutRequest request) {
    String headerImageUrl = null;
    if (hasText(request.headerImageFileName())) {
      headerImageUrl =
          adminFileStoragePort.generatePresignedUrl(
              request.headerImageFileName(), request.generationId() + "/");
    }

    List<AdminCachePort.CoreValueData> coreValueDataList = null;
    if (request.coreValues() != null) {
      coreValueDataList =
          request.coreValues().stream()
              .map(
                  cv -> {
                    String imageUrl =
                        hasText(cv.imageFileName())
                            ? adminFileStoragePort.generatePresignedUrl(
                                cv.imageFileName(), request.generationId() + "/coreValue/")
                            : null;
                    return new AdminCachePort.CoreValueData(
                        cv.value(), cv.description(), cv.detailDescription(), imageUrl);
                  })
              .toList();
    }

    List<AdminCachePort.MemberData> memberDataList = null;
    if (request.members() != null) {
      memberDataList =
          request.members().stream()
              .map(
                  m -> {
                    String profileImageUrl =
                        hasText(m.profileImageFileName())
                            ? adminFileStoragePort.generatePresignedUrl(
                                m.profileImageFileName(), request.generationId() + "/member/")
                            : null;
                    return new AdminCachePort.MemberData(
                        m.role(),
                        m.name(),
                        m.affiliation(),
                        m.introduction(),
                        profileImageUrl,
                        m.snsEmail(),
                        m.snsLinkedin(),
                        m.snsGithub(),
                        m.snsBehance());
                  })
              .toList();
    }

    List<AdminCachePort.ActivityScheduleData> activityScheduleDataList = null;
    if (request.activitySchedules() != null) {
      activityScheduleDataList =
          request.activitySchedules().stream()
              .map(
                  s ->
                      new AdminCachePort.ActivityScheduleData(
                          s.name(),
                          s.startDate() != null ? s.startDate().toString() : null,
                          s.endDate() != null ? s.endDate().toString() : null))
              .toList();
    }

    AdminCachePort.AboutCacheData cacheData =
        new AdminCachePort.AboutCacheData(
            request.generationId(),
            headerImageUrl,
            coreValueDataList,
            memberDataList,
            activityScheduleDataList);
    adminCachePort.putAbout(request.generationId(), cacheData);

    return new AboutResult(request.generationId(), headerImageUrl, coreValueDataList, memberDataList);
  }

  @Transactional
  public ConfirmResult addAboutDataConfirm(Integer generationId) {
    AdminCachePort.AboutCacheData cached = adminCachePort.getAbout(generationId);
    if (cached == null) {
      throw new IllegalStateException("소개 탭 캐시 데이터 없음. generation=" + generationId);
    }

    if (cached.headerImageUrl() != null) {
      generationService.updateHeaderImage(
          generationId, adminFileStoragePort.getOriginalUrl(cached.headerImageUrl()));
    }

    if (cached.coreValues() != null) {
      List<CoreValue> coreValues =
          IntStream.range(0, cached.coreValues().size())
              .mapToObj(
                  i -> {
                    AdminCachePort.CoreValueData cv = cached.coreValues().get(i);
                    String imageUrl =
                        cv.imageUrl() != null
                            ? adminFileStoragePort.getOriginalUrl(cv.imageUrl())
                            : null;
                    return new CoreValue(
                        null, generationId, cv.value(), cv.description(), cv.detailDescription(),
                        imageUrl, i);
                  })
              .toList();
      coreValueService.bulkCreate(generationId, coreValues);
    }

    if (cached.members() != null) {
      List<Member> members =
          cached.members().stream()
              .map(
                  m -> {
                    String profileImageUrl =
                        m.profileImageUrl() != null
                            ? adminFileStoragePort.getOriginalUrl(m.profileImageUrl())
                            : null;
                    return new Member(
                        null,
                        generationId,
                        org.sopt.makers.domain.official.member.MemberRole.fromString(m.role()),
                        m.name(),
                        m.affiliation(),
                        m.introduction(),
                        profileImageUrl,
                        SnsLinks.of(m.snsEmail(), m.snsLinkedin(), m.snsGithub(), m.snsBehance()));
                  })
              .toList();
      memberService.bulkCreate(
          new MemberService.BulkCreateMembersCommand(
              generationId,
              members.stream()
                  .map(
                      m ->
                          new MemberService.BulkCreateMembersCommand.MemberData(
                              m.role().name(),
                              m.name(),
                              m.affiliation(),
                              m.introduction(),
                              m.profileImageUrl(),
                              new MemberService.BulkCreateMembersCommand.SnsLinksData(
                                  m.snsLinks().email(),
                                  m.snsLinks().linkedin(),
                                  m.snsLinks().github(),
                                  m.snsLinks().behance())))
                  .toList()));
    }

    if (cached.activitySchedules() != null) {
      List<ActivityScheduleService.BulkCreateActivitySchedulesCommand.ActivityScheduleData>
          scheduleData =
              cached.activitySchedules().stream()
                  .map(
                      s ->
                          new ActivityScheduleService.BulkCreateActivitySchedulesCommand
                              .ActivityScheduleData(
                              s.name(),
                              s.startDate() != null ? LocalDate.parse(s.startDate()) : null,
                              s.endDate() != null ? LocalDate.parse(s.endDate()) : null))
                  .toList();
      activityScheduleService.bulkCreate(
          new ActivityScheduleService.BulkCreateActivitySchedulesCommand(generationId, scheduleData));
    }

    adminCachePort.evictAbout(generationId);
    return new ConfirmResult("소개 탭 배포 성공");
  }

  // ============================================================
  // 모집안내 탭
  // ============================================================

  @Transactional
  public RecruitResult addRecruitData(AddRecruitRequest request) {
    String recruitHeaderImageUrl = null;
    if (hasText(request.recruitHeaderImageFileName())) {
      recruitHeaderImageUrl =
          adminFileStoragePort.generatePresignedUrl(
              request.recruitHeaderImageFileName(), request.generationId() + "/");
    }

    AdminCachePort.RecruitCacheData cacheData =
        new AdminCachePort.RecruitCacheData(
            request.generationId(),
            recruitHeaderImageUrl,
            request.partIntroductions(),
            request.partCurriculums(),
            request.recruitPartCurriculums(),
            request.recruitQuestions());
    adminCachePort.putRecruit(request.generationId(), cacheData);

    return new RecruitResult(request.generationId(), recruitHeaderImageUrl);
  }

  @Transactional
  public ConfirmResult addRecruitDataConfirm(Integer generationId) {
    AdminCachePort.RecruitCacheData cached = adminCachePort.getRecruit(generationId);
    if (cached == null) {
      throw new IllegalStateException("모집안내 탭 캐시 데이터 없음. generation=" + generationId);
    }

    if (cached.recruitHeaderImageUrl() != null) {
      generationService.updateRecruitHeaderImage(
          generationId, adminFileStoragePort.getOriginalUrl(cached.recruitHeaderImageUrl()));
    }

    if (cached.partIntroductions() != null || cached.partCurriculums() != null) {
      List<RecruitPartInfo> parts = buildPartInfoList(generationId, cached);
      partService.bulkCreate(generationId, parts);
    }

    if (cached.recruitPartCurriculums() != null) {
      List<RecruitPartIntroduction> introductions =
          cached.recruitPartCurriculums().stream()
              .map(
                  rpc -> {
                    String preference =
                        rpc.introduction() != null ? rpc.introduction().preference() : null;
                    String content =
                        rpc.introduction() != null ? rpc.introduction().content() : null;
                    return RecruitPartIntroduction.from(
                        null, generationId, Part.valueOf(rpc.part()), content, preference);
                  })
              .toList();
      recruitPartIntroductionService.bulkCreate(generationId, introductions);
    }

    if (cached.recruitQuestions() != null) {
      List<FaqService.BulkCreateFaqData> faqData =
          cached.recruitQuestions().stream()
              .map(
                  rq ->
                      new FaqService.BulkCreateFaqData(
                          rq.part(),
                          rq.questions().stream()
                              .map(
                                  q ->
                                      new FaqService.BulkCreateFaqData.QuestionData(
                                          q.question(), q.answer()))
                              .toList()))
              .toList();
      faqService.bulkCreate(faqData);
    }

    adminCachePort.evictRecruit(generationId);
    return new ConfirmResult("모집안내 탭 배포 성공");
  }

  // ============================================================
  // Helpers
  // ============================================================

  private List<RecruitPartInfo> buildPartInfoList(
      Integer generationId, AdminCachePort.RecruitCacheData cached) {
    if (cached.partIntroductions() != null && cached.partCurriculums() != null) {
      return IntStream.range(0, cached.partIntroductions().size())
          .mapToObj(
              i -> {
                AdminCachePort.PartIntroductionData pi = cached.partIntroductions().get(i);
                List<String> curriculums =
                    cached.partCurriculums().stream()
                        .filter(pc -> pc.part().equals(pi.part()))
                        .findFirst()
                        .map(AdminCachePort.PartCurriculumData::curriculums)
                        .orElse(List.of());
                return new RecruitPartInfo(
                    null, generationId, Part.valueOf(pi.part()), pi.description(), curriculums);
              })
          .toList();
    }
    if (cached.partIntroductions() != null) {
      return cached.partIntroductions().stream()
          .map(
              pi ->
                  new RecruitPartInfo(
                      null, generationId, Part.valueOf(pi.part()), pi.description(), List.of()))
          .toList();
    }
    return cached.partCurriculums().stream()
        .map(
            pc ->
                new RecruitPartInfo(
                    null,
                    generationId,
                    Part.valueOf(pc.part()),
                    pc.part() + " 파트입니다.",
                    pc.curriculums()))
        .toList();
  }

  private String stripHash(String hexColor) {
    if (hexColor != null && hexColor.startsWith("#")) {
      return hexColor.substring(1);
    }
    return hexColor;
  }

  private boolean hasText(String s) {
    return s != null && !s.isBlank();
  }

  // ============================================================
  // Request / Response types
  // ============================================================

  public record AddCommonRequest(
      Integer generationId,
      String name,
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor,
      List<AdminCachePort.RecruitScheduleData> recruitSchedules) {}

  public record AddHomeRequest(
      Integer generationId,
      String homeHeaderImageFileName,
      List<NewsFileRequest> news,
      List<ReviewRequest> reviews) {
    public record NewsFileRequest(String title, String link, String imageFileName) {}

    public record ReviewRequest(String title, String content, String authorInfo) {}
  }

  public record AddAboutRequest(
      Integer generationId,
      String headerImageFileName,
      List<CoreValueRequest> coreValues,
      List<MemberRequest> members,
      List<ActivityScheduleRequest> activitySchedules) {
    public record CoreValueRequest(
        String value, String description, String detailDescription, String imageFileName) {}

    public record MemberRequest(
        String role,
        String name,
        String affiliation,
        String introduction,
        String profileImageFileName,
        String snsEmail,
        String snsLinkedin,
        String snsGithub,
        String snsBehance) {}

    public record ActivityScheduleRequest(String name, LocalDate startDate, LocalDate endDate) {}
  }

  public record AddRecruitRequest(
      Integer generationId,
      String recruitHeaderImageFileName,
      List<AdminCachePort.PartIntroductionData> partIntroductions,
      List<AdminCachePort.PartCurriculumData> partCurriculums,
      List<AdminCachePort.RecruitPartCurriculumData> recruitPartCurriculums,
      List<AdminCachePort.RecruitQuestionData> recruitQuestions) {}

  public record CommonResult(Integer generationId) {}

  public record HomeResult(
      Integer generationId,
      String homeHeaderImagePresignedUrl,
      List<AdminCachePort.NewsData> news) {}

  public record AboutResult(
      Integer generationId,
      String headerImagePresignedUrl,
      List<AdminCachePort.CoreValueData> coreValues,
      List<AdminCachePort.MemberData> members) {}

  public record RecruitResult(Integer generationId, String recruitHeaderImagePresignedUrl) {}

  public record ConfirmResult(String message) {}

  public record AdminMainData(
      Generation generation,
      List<CoreValue> coreValues,
      List<Member> members,
      List<Recruitment> recruitments,
      List<ActivitySchedule> activitySchedules,
      List<RecruitPartInfo> partInfos,
      List<RecruitPartIntroduction> recruitPartIntroductions,
      List<Faq> faqs,
      List<org.sopt.makers.domain.official.news.News> news,
      List<org.sopt.makers.domain.official.homepage.review.HomepageReview> reviews) {}
}
