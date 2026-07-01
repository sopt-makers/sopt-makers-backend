package org.sopt.makers.api.controller.admin.official.dto;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.admin.AdminOfficialFacade;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruitment.Recruitment;

public class AdminOfficialResponse {

  public record AdminMainResponse(
      GenerationData generation,
      List<CoreValueData> coreValues,
      List<MemberData> members,
      List<RecruitmentData> recruitments,
      List<ActivityScheduleData> activitySchedules,
      List<PartInfoData> partInfos,
      List<RecruitPartIntroductionData> recruitPartIntroductions,
      List<FaqData> faqs,
      List<NewsData> news,
      List<ReviewData> reviews) {

    public static AdminMainResponse from(AdminOfficialFacade.AdminMainData data) {
      return new AdminMainResponse(
          GenerationData.from(data.generation()),
          data.coreValues().stream().map(CoreValueData::from).toList(),
          data.members().stream().map(MemberData::from).toList(),
          data.recruitments().stream().map(RecruitmentData::from).toList(),
          data.activitySchedules().stream().map(ActivityScheduleData::from).toList(),
          data.partInfos().stream().map(PartInfoData::from).toList(),
          data.recruitPartIntroductions().stream().map(RecruitPartIntroductionData::from).toList(),
          data.faqs().stream().map(FaqData::from).toList(),
          data.news().stream().map(NewsData::from).toList(),
          data.reviews().stream().map(ReviewData::from).toList());
    }
  }

  public record GenerationData(
      Integer id,
      String name,
      String headerImage,
      String recruitHeaderImage,
      String homeHeaderImage,
      BrandingColorData brandingColor) {

    public static GenerationData from(Generation generation) {
      if (generation == null) return null;
      BrandingColorData bc =
          generation.brandingColor() == null
              ? null
              : new BrandingColorData(
                  generation.brandingColor().darkModeKeyColor(),
                  generation.brandingColor().darkModeTextColor(),
                  generation.brandingColor().lightModeKeyColor(),
                  generation.brandingColor().lightModeTextColor());
      return new GenerationData(
          generation.id(),
          generation.name(),
          generation.headerImage(),
          generation.recruitHeaderImage(),
          generation.homeHeaderImage(),
          bc);
    }
  }

  public record BrandingColorData(
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor) {}

  public record CoreValueData(
      Long id,
      Integer generationId,
      String value,
      String description,
      String detailDescription,
      String imageUrl,
      int displayOrder) {

    public static CoreValueData from(CoreValue cv) {
      return new CoreValueData(
          cv.id(),
          cv.generationId(),
          cv.value(),
          cv.description(),
          cv.detailDescription(),
          cv.imageUrl(),
          cv.displayOrder());
    }
  }

  public record MemberData(
      Long id,
      Integer generationId,
      String role,
      String name,
      String affiliation,
      String introduction,
      String profileImageUrl,
      SnsLinksData snsLinks) {

    public static MemberData from(Member m) {
      SnsLinksData sns =
          m.snsLinks() == null
              ? null
              : new SnsLinksData(
                  m.snsLinks().email(),
                  m.snsLinks().linkedin(),
                  m.snsLinks().github(),
                  m.snsLinks().behance());
      return new MemberData(
          m.id(),
          m.generationId(),
          m.role() != null ? m.role().name() : null,
          m.name(),
          m.affiliation(),
          m.introduction(),
          m.profileImageUrl(),
          sns);
    }
  }

  public record SnsLinksData(String email, String linkedin, String github, String behance) {}

  public record RecruitmentData(
      Long id, Integer generationId, String recruitType, ScheduleData schedule) {

    public static RecruitmentData from(Recruitment r) {
      ScheduleData s =
          r.schedule() == null
              ? null
              : new ScheduleData(
                  r.schedule().applicationStartTime(),
                  r.schedule().applicationEndTime(),
                  r.schedule().applicationResultTime(),
                  r.schedule().interviewStartTime(),
                  r.schedule().interviewEndTime(),
                  r.schedule().finalResultTime());
      return new RecruitmentData(
          r.id(), r.generationId(), r.recruitType() != null ? r.recruitType().name() : null, s);
    }
  }

  public record ScheduleData(
      String applicationStartTime,
      String applicationEndTime,
      String applicationResultTime,
      String interviewStartTime,
      String interviewEndTime,
      String finalResultTime) {}

  public record ActivityScheduleData(
      Long id, Integer generationId, String name, LocalDate startDate, LocalDate endDate) {

    public static ActivityScheduleData from(ActivitySchedule s) {
      return new ActivityScheduleData(
          s.id(), s.generationId(), s.name(), s.startDate(), s.endDate());
    }
  }

  public record PartInfoData(
      Long id, Integer generationId, String partType, String description, List<String> curriculums) {

    public static PartInfoData from(RecruitPartInfo p) {
      return new PartInfoData(
          p.id(),
          p.generationId(),
          p.part() != null ? p.part().name() : null,
          p.description(),
          p.curriculums());
    }
  }

  public record RecruitPartIntroductionData(
      Long id, Integer generationId, String part, String content, List<String> preferences) {

    public static RecruitPartIntroductionData from(RecruitPartIntroduction intro) {
      return new RecruitPartIntroductionData(
          intro.id(),
          intro.generationId(),
          intro.part() != null ? intro.part().name() : null,
          intro.content(),
          intro.preferences());
    }
  }

  public record FaqData(Long id, String part, List<QuestionAnswerData> questions) {

    public static FaqData from(Faq faq) {
      return new FaqData(
          faq.id(),
          faq.part() != null ? faq.part().name() : null,
          faq.questions() == null
              ? null
              : faq.questions().stream()
                  .map(qa -> new QuestionAnswerData(qa.question(), qa.answer()))
                  .toList());
    }
  }

  public record QuestionAnswerData(String question, String answer) {}

  public record NewsData(Integer id, String imageUrl, String title, String link) {

    public static NewsData from(News n) {
      return new NewsData(n.id(), n.imageUrl(), n.title(), n.link());
    }
  }

  public record ReviewData(Long id, String title, String content, String authorInfo) {

    public static ReviewData from(HomepageReview r) {
      return new ReviewData(r.id(), r.title(), r.content(), r.authorInfo());
    }
  }

  public record AddCommonResponse(Integer generationId) {

    public static AddCommonResponse from(AdminOfficialFacade.CommonResult result) {
      return new AddCommonResponse(result.generationId());
    }
  }

  public record AddHomeResponse(
      Integer generationId,
      String homeHeaderImagePresignedUrl,
      List<NewsPresignedData> news) {

    public static AddHomeResponse from(AdminOfficialFacade.HomeResult result) {
      List<NewsPresignedData> newsData =
          result.news() == null
              ? null
              : result.news().stream()
                  .map(n -> new NewsPresignedData(n.title(), n.link(), n.imageUrl()))
                  .toList();
      return new AddHomeResponse(result.generationId(), result.homeHeaderImagePresignedUrl(), newsData);
    }
  }

  public record NewsPresignedData(String title, String link, String imagePresignedUrl) {}

  public record AddAboutResponse(
      Integer generationId,
      String headerImagePresignedUrl,
      List<CoreValuePresignedData> coreValues,
      List<MemberPresignedData> members) {

    public static AddAboutResponse from(AdminOfficialFacade.AboutResult result) {
      List<CoreValuePresignedData> cvData =
          result.coreValues() == null
              ? null
              : result.coreValues().stream()
                  .map(
                      cv ->
                          new CoreValuePresignedData(
                              cv.value(), cv.description(), cv.detailDescription(), cv.imageUrl()))
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

  public record CoreValuePresignedData(
      String value, String description, String detailDescription, String imagePresignedUrl) {}

  public record MemberPresignedData(String role, String name, String profileImagePresignedUrl) {}

  public record AddRecruitResponse(
      Integer generationId, String recruitHeaderImagePresignedUrl) {

    public static AddRecruitResponse from(AdminOfficialFacade.RecruitResult result) {
      return new AddRecruitResponse(
          result.generationId(), result.recruitHeaderImagePresignedUrl());
    }
  }

  public record ConfirmResponse(String message) {

    public static ConfirmResponse from(AdminOfficialFacade.ConfirmResult result) {
      return new ConfirmResponse(result.message());
    }
  }

  private AdminOfficialResponse() {}
}
