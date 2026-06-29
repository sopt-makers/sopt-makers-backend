package org.sopt.makers.api.controller.official.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.recruit.RecruitPart;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.facade.RecruitPageFacade;

public final class RecruitPageResponse {

  private RecruitPageResponse() {}

  @Schema(description = "지원서 메인 페이지 응답")
  public record MainPage(
      String recruitHeaderImage,
      BrandingColor brandingColor,
      List<PartIntroduction> partIntroduction,
      List<CoreValueResponse> coreValue,
      List<RecruitQuestion> recruitQuestion) {

    public static MainPage from(RecruitPageFacade.RecruitMainPage mainPage) {
      return new MainPage(
          mainPage.generation().recruitHeaderImage(),
          BrandingColor.from(mainPage.generation().brandingColor()),
          mainPage.partIntroductions().stream().map(PartIntroduction::from).toList(),
          mainPage.coreValues().stream().map(CoreValueResponse::from).toList(),
          mainPage.faqs().stream().map(RecruitQuestion::from).toList());
    }
  }

  public record PartDetail(
      String introduction, List<String> preferences, List<String> partCurriculum) {

    public static PartDetail from(RecruitPageFacade.RecruitPartDetail partDetail) {
      return new PartDetail(
          partDetail.introduction(),
          parsePreferences(partDetail.preference()),
          partDetail.partCurriculum());
    }

    private static List<String> parsePreferences(String preference) {
      if (preference == null || preference.isBlank()) {
        return List.of();
      }
      return preference
          .lines()
          .map(line -> line.replaceFirst("^-\\s*", "").trim())
          .filter(line -> !line.isBlank())
          .toList();
    }
  }

  public record BrandingColor(
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor) {

    public static BrandingColor from(
        org.sopt.makers.domain.official.generation.BrandingColor color) {
      if (color == null) {
        return null;
      }
      return new BrandingColor(
          color.darkModeKeyColor(),
          color.darkModeTextColor(),
          color.lightModeKeyColor(),
          color.lightModeTextColor());
    }
  }

  public record PartIntroduction(String part, String description) {

    public static PartIntroduction from(RecruitPartInfo partInfo) {
      return new PartIntroduction(RecruitPart.displayName(partInfo.part()), partInfo.description());
    }
  }

  public record CoreValueResponse(String value, String description, String image) {

    public static CoreValueResponse from(CoreValue coreValue) {
      return new CoreValueResponse(
          coreValue.value(), coreValue.description(), coreValue.imageUrl());
    }
  }

  public record RecruitQuestion(String part, List<Question> questions) {

    public static RecruitQuestion from(Faq faq) {
      return new RecruitQuestion(
          RecruitPart.displayName(faq.part()),
          faq.questions().stream().map(Question::from).toList());
    }
  }

  public record Question(String question, String answer) {

    public static Question from(Faq.QuestionAnswer questionAnswer) {
      return new Question(questionAnswer.question(), questionAnswer.answer());
    }
  }
}
