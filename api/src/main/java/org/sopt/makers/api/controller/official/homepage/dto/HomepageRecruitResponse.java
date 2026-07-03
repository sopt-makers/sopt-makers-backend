package org.sopt.makers.api.controller.official.homepage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.homepage.facade.HomepageFacade;
import org.sopt.makers.domain.official.recruit.RecruitPart;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruitment.Recruitment;

@Schema(description = "Recruiting 페이지 데이터")
public record HomepageRecruitResponse(
    @Schema(description = "기수") int generation,
    @Schema(description = "기수명") String name,
    @Schema(description = "모집 헤더 이미지 URL") String recruitHeaderImage,
    BrandingColor brandingColor,
    List<RecruitSchedule> recruitSchedule,
    List<RecruitPartCurriculum> recruitPartCurriculum,
    List<RecruitQuestion> recruitQuestion) {

  public static HomepageRecruitResponse from(HomepageFacade.RecruitPageData data) {
    return new HomepageRecruitResponse(
        data.generation().id(),
        data.generation().name(),
        data.generation().recruitHeaderImage(),
        BrandingColor.from(data.generation()),
        data.recruitments().stream().map(RecruitSchedule::from).toList(),
        data.recruitPartIntros().stream().map(RecruitPartCurriculum::from).toList(),
        data.faqs().stream().map(RecruitQuestion::from).toList());
  }

  public record BrandingColor(
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor) {

    public static BrandingColor from(
        org.sopt.makers.domain.official.generation.Generation generation) {
      if (generation.brandingColor() == null) {
        return null;
      }
      return new BrandingColor(
          generation.brandingColor().darkModeKeyColor(),
          generation.brandingColor().darkModeTextColor(),
          generation.brandingColor().lightModeKeyColor(),
          generation.brandingColor().lightModeTextColor());
    }
  }

  public record RecruitSchedule(String type, Schedule schedule) {

    public static RecruitSchedule from(Recruitment recruitment) {
      return new RecruitSchedule(
          recruitment.recruitType().getCode(), Schedule.from(recruitment.schedule()));
    }

    public record Schedule(
        String applicationStartTime,
        String applicationEndTime,
        String applicationResultTime,
        String interviewStartTime,
        String interviewEndTime,
        String finalResultTime) {

      public static Schedule from(org.sopt.makers.domain.official.recruitment.Schedule schedule) {
        if (schedule == null) {
          return null;
        }
        return new Schedule(
            schedule.applicationStartTime(),
            schedule.applicationEndTime(),
            schedule.applicationResultTime(),
            schedule.interviewStartTime(),
            schedule.interviewEndTime(),
            schedule.finalResultTime());
      }
    }
  }

  public record RecruitPartCurriculum(String part, Introduction introduction) {

    public static RecruitPartCurriculum from(RecruitPartIntroduction intro) {
      return new RecruitPartCurriculum(
          RecruitPart.displayName(intro.part()), Introduction.from(intro));
    }

    public record Introduction(String content, String preference) {

      public static Introduction from(RecruitPartIntroduction intro) {
        String preference =
            intro.preferences() == null || intro.preferences().isEmpty()
                ? null
                : intro.preferences().stream().map(p -> "- " + p).collect(Collectors.joining("\n"));
        return new Introduction(intro.content(), preference);
      }
    }
  }

  public record RecruitQuestion(String part, List<Question> questions) {

    public static RecruitQuestion from(Faq faq) {
      return new RecruitQuestion(
          RecruitPart.displayName(faq.part()),
          faq.questions().stream().map(Question::from).toList());
    }

    public record Question(String question, String answer) {

      public static Question from(Faq.QuestionAnswer qa) {
        return new Question(qa.question(), qa.answer());
      }
    }
  }
}
