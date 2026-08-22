package org.sopt.makers.api.controller.official.homepage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.official.homepage.facade.HomepageFacade;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.recruit.RecruitPart;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruitment.Recruitment;

@Schema(description = "메인 페이지 데이터")
public record HomepageMainResponse(
    @Schema(description = "기수") int generation,
    @Schema(description = "기수명") String name,
    BrandingColor brandingColor,
    List<PartIntroduction> partIntroduction,
    List<LatestNews> latestNews,
    List<RecruitSchedule> recruitSchedule,
    ActivitiesRecords activitiesRecords,
    List<Review> reviews) {

  public static HomepageMainResponse from(HomepageFacade.MainPageData data) {
    return new HomepageMainResponse(
        data.generation().id(),
        data.generation().name(),
        BrandingColor.from(data.generation()),
        data.partInfos().stream().map(PartIntroduction::from).toList(),
        data.news().stream().map(LatestNews::from).toList(),
        data.recruitments().stream().map(RecruitSchedule::from).toList(),
        ActivitiesRecords.from(data.activitiesRecords()),
        data.reviews().stream().map(Review::from).toList());
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

  public record PartIntroduction(String part, String description) {

    public static PartIntroduction from(RecruitPartInfo partInfo) {
      return new PartIntroduction(RecruitPart.displayName(partInfo.part()), partInfo.description());
    }
  }

  public record LatestNews(int id, String title, String image, String link) {

    public static LatestNews from(News news) {
      return new LatestNews(news.id(), news.title(), news.imageUrl(), news.link());
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

  @Schema(description = "활동 기록")
  public record ActivitiesRecords(
      @Schema(description = "활동 회원 수") int activitiesUserCount,
      @Schema(description = "프로젝트 수") int projectCounts,
      @Schema(description = "스터디 수") int studyCounts,
      @Schema(description = "운영 기간") int operationPeriod) {

    public static ActivitiesRecords from(HomepageFacade.ActivitiesRecords records) {
      return new ActivitiesRecords(
          records.activitiesUserCount(),
          records.projectCounts(),
          records.studyCounts(),
          records.operationPeriod());
    }
  }

  public record Review(
      @Schema(description = "ID") int id,
      @Schema(description = "제목") String title,
      @Schema(description = "내용") String content,
      @Schema(description = "작성자 정보") String authorInfo) {

    public static Review from(HomepageReview review) {
      return new Review(
          review.id().intValue(), review.title(), review.content(), review.authorInfo());
    }
  }
}
