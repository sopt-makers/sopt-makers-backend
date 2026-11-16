package org.sopt.makers.api.controller.admin.official.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.admin.facade.AdminOfficialFacade;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.sopt.makers.domain.official.faq.FaqCategory;
import org.sopt.makers.domain.official.homepage.review.HomepageReviewType;

public class AdminOfficialRequest {

  public record AddCommonRequest(
      @NotNull @Positive Integer generation,
      @NotEmpty String name,
      @NotNull @Valid BrandingColorRequest brandingColor,
      @Valid List<RecruitScheduleRequest> recruitSchedule) {

    public record RecruitScheduleRequest(
        @Pattern(regexp = "^(OB|YB)$") String type, @Valid ScheduleRequest schedule) {}

    public record ScheduleRequest(
        String applicationStartTime,
        String applicationEndTime,
        String applicationResultTime,
        String interviewStartTime,
        String interviewEndTime,
        String finalResultTime) {}

    public AdminOfficialFacade.AddCommonRequest toCommand() {
      List<AdminCachePort.RecruitScheduleData> schedules =
          recruitSchedule == null
              ? null
              : recruitSchedule.stream()
                  .map(
                      rs ->
                          new AdminCachePort.RecruitScheduleData(
                              rs.type(),
                              rs.schedule() == null
                                  ? null
                                  : new AdminCachePort.ScheduleData(
                                      rs.schedule().applicationStartTime(),
                                      rs.schedule().applicationEndTime(),
                                      rs.schedule().applicationResultTime(),
                                      rs.schedule().interviewStartTime(),
                                      rs.schedule().interviewEndTime(),
                                      rs.schedule().finalResultTime())))
                  .toList();
      return new AdminOfficialFacade.AddCommonRequest(
          generation,
          name,
          brandingColor.darkModeKeyColor(),
          brandingColor.darkModeTextColor(),
          brandingColor.lightModeKeyColor(),
          brandingColor.lightModeTextColor(),
          schedules);
    }
  }

  public record AddHomeRequest(
      @NotNull @Positive Integer generation,
      String homeHeaderImageFileName,
      @Valid List<NewsRequest> news,
      @Valid List<ReviewRequest> review) {

    public record NewsRequest(
        @NotEmpty String title, @NotEmpty String link, @NotNull String imageFileName) {}

    public record ReviewRequest(
        @NotBlank @Size(max = 10) String title,
        @NotBlank @Size(max = 200) String content,
        @NotBlank String authorInfo,
        HomepageReviewType type) {}

    public AdminOfficialFacade.AddHomeRequest toCommand() {
      List<AdminOfficialFacade.AddHomeRequest.NewsFileRequest> newsRequests =
          news == null
              ? null
              : news.stream()
                  .map(
                      n ->
                          new AdminOfficialFacade.AddHomeRequest.NewsFileRequest(
                              n.title(), n.link(), n.imageFileName()))
                  .toList();
      List<AdminOfficialFacade.AddHomeRequest.ReviewRequest> reviewRequests =
          review == null
              ? null
              : review.stream()
                  .map(
                      r ->
                          new AdminOfficialFacade.AddHomeRequest.ReviewRequest(
                              r.title(), r.content(), r.authorInfo(), r.type()))
                  .toList();
      return new AdminOfficialFacade.AddHomeRequest(
          generation, homeHeaderImageFileName, newsRequests, reviewRequests);
    }
  }

  public record AddAboutRequest(
      @NotNull @Positive Integer generation,
      String headerImageFileName,
      @Valid List<CoreValueRequest> coreValue,
      @Valid List<MemberRequest> member,
      @Valid List<ActivityScheduleRequest> activitySchedule) {

    public record CoreValueRequest(
        @NotBlank String value,
        @NotBlank String description,
        @NotBlank String detailDescription,
        String imageFileName) {}

    public record MemberRequest(
        @NotBlank String role,
        @NotBlank String name,
        @NotBlank String affiliation,
        @NotBlank String introduction,
        String profileImageFileName,
        @Valid SnsRequest sns) {}

    public record ActivityScheduleRequest(
        @NotBlank String name, @NotNull LocalDate startDate, LocalDate endDate) {}

    public AdminOfficialFacade.AddAboutRequest toCommand() {
      List<AdminOfficialFacade.AddAboutRequest.CoreValueRequest> cvRequests =
          coreValue == null
              ? null
              : coreValue.stream()
                  .map(
                      cv ->
                          new AdminOfficialFacade.AddAboutRequest.CoreValueRequest(
                              cv.value(),
                              cv.description(),
                              cv.detailDescription(),
                              cv.imageFileName()))
                  .toList();
      List<AdminOfficialFacade.AddAboutRequest.MemberRequest> memberRequests =
          member == null
              ? null
              : member.stream()
                  .map(
                      m ->
                          new AdminOfficialFacade.AddAboutRequest.MemberRequest(
                              m.role(),
                              m.name(),
                              m.affiliation(),
                              m.introduction(),
                              m.profileImageFileName(),
                              m.sns() == null ? null : m.sns().email(),
                              m.sns() == null ? null : m.sns().linkedin(),
                              m.sns() == null ? null : m.sns().github(),
                              m.sns() == null ? null : m.sns().behance()))
                  .toList();
      List<AdminOfficialFacade.AddAboutRequest.ActivityScheduleRequest> scheduleRequests =
          activitySchedule == null
              ? null
              : activitySchedule.stream()
                  .map(
                      s ->
                          new AdminOfficialFacade.AddAboutRequest.ActivityScheduleRequest(
                              s.name(), s.startDate(), s.endDate()))
                  .toList();
      return new AdminOfficialFacade.AddAboutRequest(
          generation, headerImageFileName, cvRequests, memberRequests, scheduleRequests);
    }
  }

  public record AddRecruitRequest(
      @NotNull @Positive Integer generation,
      String recruitHeaderImageFileName,
      @Valid List<PartIntroductionRequest> partIntroduction,
      @Valid List<PartCurriculumRequest> partCurriculum,
      @Valid List<RecruitPartCurriculumRequest> recruitPartCurriculum,
      @Valid List<RecruitQuestionRequest> recruitQuestion,
      @Valid List<@NotNull FaqRequest> faq) {

    public record PartIntroductionRequest(@NotBlank String part, @NotBlank String description) {}

    public record PartCurriculumRequest(
        @NotBlank String part, @NotEmpty List<String> curriculums) {}

    public record RecruitPartCurriculumRequest(
        @NotBlank String part, @NotNull @Valid IntroductionRequest introduction) {}

    public record IntroductionRequest(@NotBlank String content, @NotBlank String preference) {}

    public record RecruitQuestionRequest(
        @NotBlank String part, @NotEmpty @Valid List<QuestionRequest> questions) {}

    public AdminOfficialFacade.AddRecruitRequest toCommand() {
      List<AdminCachePort.PartIntroductionData> piData =
          partIntroduction == null
              ? null
              : partIntroduction.stream()
                  .map(pi -> new AdminCachePort.PartIntroductionData(pi.part(), pi.description()))
                  .toList();
      List<AdminCachePort.PartCurriculumData> pcData =
          partCurriculum == null
              ? null
              : partCurriculum.stream()
                  .map(pc -> new AdminCachePort.PartCurriculumData(pc.part(), pc.curriculums()))
                  .toList();
      List<AdminCachePort.RecruitPartCurriculumData> rpcData =
          recruitPartCurriculum == null
              ? null
              : recruitPartCurriculum.stream()
                  .map(
                      rpc ->
                          new AdminCachePort.RecruitPartCurriculumData(
                              rpc.part(),
                              rpc.introduction() == null
                                  ? null
                                  : new AdminCachePort.IntroductionData(
                                      rpc.introduction().content(),
                                      rpc.introduction().preference())))
                  .toList();
      List<AdminCachePort.FaqData> faqData = null;
      if (faq != null) {
        faqData =
            faq.stream()
                .map(
                    f ->
                        new AdminCachePort.FaqData(
                            Arrays.stream(FaqCategory.values())
                                .filter(
                                    category ->
                                        category.getDisplayName().equals(f.category().trim()))
                                .findFirst()
                                .orElseThrow(),
                            f.questions().stream()
                                .map(q -> new AdminCachePort.QuestionData(q.question(), q.answer()))
                                .toList()))
                .toList();
      } else if (recruitQuestion != null) {
        faqData =
            recruitQuestion.stream()
                .map(
                    f ->
                        new AdminCachePort.FaqData(
                            FaqCategory.valueOf(Part.fromString(f.part()).name()),
                            f.questions().stream()
                                .map(q -> new AdminCachePort.QuestionData(q.question(), q.answer()))
                                .toList()))
                .toList();
      }
      return new AdminOfficialFacade.AddRecruitRequest(
          generation, recruitHeaderImageFileName, piData, pcData, rpcData, faqData);
    }
  }

  public record BrandingColorRequest(
      @NotNull @Pattern(regexp = "^#?[A-Fa-f0-9]{6}$") String darkModeKeyColor,
      @NotNull @Pattern(regexp = "^(white|black)$") String darkModeTextColor,
      @NotNull @Pattern(regexp = "^#?[A-Fa-f0-9]{6}$") String lightModeKeyColor,
      @NotNull @Pattern(regexp = "^(white|black)$") String lightModeTextColor) {}

  public record SnsRequest(
      @Email String email,
      @Pattern(regexp = "^https?://.*") String linkedin,
      @Pattern(regexp = "^https?://.*") String github,
      @Pattern(regexp = "^https?://.*") String behance) {}

  public record QuestionRequest(@NotBlank String question, @NotBlank String answer) {}

  public record FaqRequest(
      @NotBlank @Pattern(regexp = "^\\s*(지원자격|면접|활동)\\s*$") String category,
      @NotEmpty List<@NotNull @Valid QuestionRequest> questions) {}

  public record ConfirmRequest(@NotNull @Positive Integer generation) {}

  private AdminOfficialRequest() {}
}
