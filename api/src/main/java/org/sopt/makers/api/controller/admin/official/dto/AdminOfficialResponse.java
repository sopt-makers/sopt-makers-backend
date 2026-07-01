package org.sopt.makers.api.controller.admin.official.dto;

import java.util.List;
import org.sopt.makers.domain.official.admin.facade.AdminOfficialFacade;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;

public class AdminOfficialResponse {

  // ============================================================
  // GET 전체 조회 (원본 flat 구조 유지)
  // ============================================================

  public record AdminMainResponse(
      int generation,
      String name,
      List<RecruitScheduleData> recruitSchedule,
      BrandingColorData brandingColor,
      List<PartIntroductionData> partIntroduction,
      List<LatestNewsData> latestNews,
      String headerImage,
      String homeHeaderImage,
      List<CoreValueData> coreValue,
      List<PartCurriculumData> partCurriculum,
      List<MemberData> member,
      String recruitHeaderImage,
      List<RecruitPartCurriculumData> recruitPartCurriculum,
      List<RecruitQuestionData> recruitQuestion,
      List<ReviewData> review,
      List<ActivityScheduleData> activitySchedule) {

    public static AdminMainResponse from(AdminOfficialFacade.AdminMainData data) {
      var gen = data.generation();
      BrandingColorData brandingColor =
          gen.brandingColor() == null
              ? null
              : new BrandingColorData(
                  gen.brandingColor().darkModeKeyColor(),
                  gen.brandingColor().darkModeTextColor(),
                  gen.brandingColor().lightModeKeyColor(),
                  gen.brandingColor().lightModeTextColor());

      List<RecruitScheduleData> recruitSchedule =
          data.recruitments() == null
              ? null
              : data.recruitments().stream()
                  .map(
                      r -> {
                        ScheduleData schedule =
                            r.schedule() == null
                                ? null
                                : new ScheduleData(
                                    r.schedule().applicationStartTime(),
                                    r.schedule().applicationEndTime(),
                                    r.schedule().applicationResultTime(),
                                    r.schedule().interviewStartTime(),
                                    r.schedule().interviewEndTime(),
                                    r.schedule().finalResultTime());
                        return new RecruitScheduleData(
                            r.recruitType() != null ? r.recruitType().name() : null, schedule);
                      })
                  .toList();

      List<PartIntroductionData> partIntroduction =
          data.partInfos() == null
              ? null
              : data.partInfos().stream()
                  .map(p -> new PartIntroductionData(p.part() != null ? p.part().name() : null, p.description()))
                  .toList();

      List<LatestNewsData> latestNews =
          data.news() == null
              ? null
              : data.news().stream()
                  .map(n -> new LatestNewsData(n.id(), n.title()))
                  .toList();

      List<CoreValueData> coreValue =
          data.coreValues() == null
              ? null
              : data.coreValues().stream()
                  .map(cv -> new CoreValueData(cv.value(), cv.description(), cv.detailDescription(), cv.imageUrl()))
                  .toList();

      List<PartCurriculumData> partCurriculum =
          data.partInfos() == null
              ? null
              : data.partInfos().stream()
                  .map(p -> new PartCurriculumData(p.part() != null ? p.part().name() : null, p.curriculums()))
                  .toList();

      List<MemberData> member =
          data.members() == null
              ? null
              : data.members().stream()
                  .map(
                      m -> {
                        SnsLinksData sns =
                            m.snsLinks() == null
                                ? null
                                : new SnsLinksData(
                                    m.snsLinks().email(),
                                    m.snsLinks().linkedin(),
                                    m.snsLinks().github(),
                                    m.snsLinks().behance());
                        return new MemberData(
                            m.role() != null ? m.role().name() : null,
                            m.name(),
                            m.affiliation(),
                            m.introduction(),
                            m.profileImageUrl(),
                            sns);
                      })
                  .toList();

      List<RecruitPartCurriculumData> recruitPartCurriculum =
          data.recruitPartIntroductions() == null
              ? null
              : data.recruitPartIntroductions().stream()
                  .map(
                      rpi -> {
                        String preference =
                            rpi.preferences() == null || rpi.preferences().isEmpty()
                                ? null
                                : String.join("\n", rpi.preferences().stream()
                                    .map(p -> "- " + p)
                                    .toList());
                        return new RecruitPartCurriculumData(
                            rpi.part() != null ? rpi.part().name() : null,
                            new IntroductionData(rpi.content(), preference));
                      })
                  .toList();

      List<RecruitQuestionData> recruitQuestion =
          data.faqs() == null
              ? null
              : data.faqs().stream()
                  .map(
                      faq ->
                          new RecruitQuestionData(
                              faq.part() != null ? faq.part().name() : null,
                              faq.questions() == null
                                  ? null
                                  : faq.questions().stream()
                                      .map(q -> new QuestionAnswerData(q.question(), q.answer()))
                                      .toList()))
                  .toList();

      List<ReviewData> review =
          data.reviews() == null
              ? null
              : data.reviews().stream()
                  .map(r -> new ReviewData(r.id(), r.title(), r.content(), r.authorInfo()))
                  .toList();

      List<ActivityScheduleData> activitySchedule =
          data.activitySchedules() == null
              ? null
              : data.activitySchedules().stream()
                  .map(
                      s ->
                          new ActivityScheduleData(
                              s.name(),
                              s.startDate() != null ? s.startDate().toString() : null,
                              s.endDate() != null ? s.endDate().toString() : null))
                  .toList();

      return new AdminMainResponse(
          gen.id(),
          gen.name(),
          recruitSchedule,
          brandingColor,
          partIntroduction,
          latestNews,
          gen.headerImage(),
          gen.homeHeaderImage(),
          coreValue,
          partCurriculum,
          member,
          gen.recruitHeaderImage(),
          recruitPartCurriculum,
          recruitQuestion,
          review,
          activitySchedule);
    }
  }

  public record BrandingColorData(
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor) {}

  public record RecruitScheduleData(String type, ScheduleData schedule) {}

  public record ScheduleData(
      String applicationStartTime,
      String applicationEndTime,
      String applicationResultTime,
      String interviewStartTime,
      String interviewEndTime,
      String finalResultTime) {}

  public record PartIntroductionData(String part, String description) {}

  public record LatestNewsData(Integer id, String title) {}

  public record CoreValueData(
      String value, String description, String detailDescription, String image) {}

  public record PartCurriculumData(String part, List<String> curriculums) {}

  public record MemberData(
      String role,
      String name,
      String affiliation,
      String introduction,
      String profileImage,
      SnsLinksData sns) {}

  public record SnsLinksData(String email, String linkedin, String github, String behance) {}

  public record RecruitPartCurriculumData(String part, IntroductionData introduction) {}

  public record IntroductionData(String content, String preference) {}

  public record RecruitQuestionData(String part, List<QuestionAnswerData> questions) {}

  public record QuestionAnswerData(String question, String answer) {}

  public record ReviewData(Long id, String title, String content, String authorInfo) {}

  public record ActivityScheduleData(String name, String startDate, String endDate) {}

  // ============================================================
  // Step 1 응답
  // ============================================================

  public record AddCommonResponse(int generation) {

    public static AddCommonResponse from(AdminOfficialFacade.CommonResult result) {
      return new AddCommonResponse(result.generationId());
    }
  }

  public record AddHomeResponse(
      int generation,
      String homeHeaderImage,
      List<NewsPresignedData> news) {

    public static AddHomeResponse from(AdminOfficialFacade.HomeResult result) {
      List<NewsPresignedData> newsData =
          result.news() == null
              ? null
              : result.news().stream()
                  .map(n -> new NewsPresignedData(n.title(), n.imageUrl()))
                  .toList();
      return new AddHomeResponse(result.generationId(), result.homeHeaderImagePresignedUrl(), newsData);
    }
  }

  public record NewsPresignedData(String title, String imagePresignedUrl) {}

  public record AddAboutResponse(
      int generation,
      String headerImage,
      List<CoreValuePresignedData> coreValues,
      List<MemberPresignedData> members) {

    public static AddAboutResponse from(AdminOfficialFacade.AboutResult result) {
      List<CoreValuePresignedData> cvData =
          result.coreValues() == null
              ? null
              : result.coreValues().stream()
                  .map(cv -> new CoreValuePresignedData(cv.value(), cv.imageUrl()))
                  .toList();
      List<MemberPresignedData> memberData =
          result.members() == null
              ? null
              : result.members().stream()
                  .map(m -> new MemberPresignedData(m.role(), m.name(), m.profileImageUrl()))
                  .toList();
      return new AddAboutResponse(
          result.generationId(), result.headerImagePresignedUrl(), cvData, memberData);
    }
  }

  public record CoreValuePresignedData(String value, String image) {}

  public record MemberPresignedData(String role, String name, String profileImage) {}

  public record AddRecruitResponse(int generation, String recruitHeaderImage) {

    public static AddRecruitResponse from(AdminOfficialFacade.RecruitResult result) {
      return new AddRecruitResponse(result.generationId(), result.recruitHeaderImagePresignedUrl());
    }
  }

  // ============================================================
  // Step 2 응답 (confirm)
  // ============================================================

  public record ConfirmResponse(String message) {

    public static ConfirmResponse from(AdminOfficialFacade.ConfirmResult result) {
      return new ConfirmResponse(result.message());
    }
  }

  private AdminOfficialResponse() {}
}
