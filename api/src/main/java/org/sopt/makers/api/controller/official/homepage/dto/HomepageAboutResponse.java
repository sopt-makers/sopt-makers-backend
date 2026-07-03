package org.sopt.makers.api.controller.official.homepage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.homepage.facade.HomepageFacade;
import org.sopt.makers.domain.official.member.Member;
import org.sopt.makers.domain.official.recruit.RecruitPart;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;

@Schema(description = "About 페이지 데이터")
public record HomepageAboutResponse(
    @Schema(description = "기수") int generation,
    @Schema(description = "기수명") String name,
    @Schema(description = "헤더 이미지 URL") String headerImage,
    BrandingColor brandingColor,
    List<CoreValueResponse> coreValue,
    List<PartCurriculum> partCurriculum,
    List<MemberResponse> member,
    List<Schedule> schedule) {

  public static HomepageAboutResponse from(HomepageFacade.AboutPageData data) {
    return new HomepageAboutResponse(
        data.generation().id(),
        data.generation().name(),
        data.generation().headerImage(),
        BrandingColor.from(data.generation()),
        data.coreValues().stream().map(CoreValueResponse::from).toList(),
        data.partInfos().stream().map(PartCurriculum::from).toList(),
        data.members().stream().map(MemberResponse::from).toList(),
        data.activitySchedules().stream().map(Schedule::from).toList());
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

  public record CoreValueResponse(String value, String description, String image) {

    public static CoreValueResponse from(CoreValue coreValue) {
      return new CoreValueResponse(
          coreValue.value(), coreValue.description(), coreValue.imageUrl());
    }
  }

  public record PartCurriculum(String part, List<String> curriculums) {

    public static PartCurriculum from(RecruitPartInfo partInfo) {
      return new PartCurriculum(RecruitPart.displayName(partInfo.part()), partInfo.curriculums());
    }
  }

  public record MemberResponse(
      String role,
      String name,
      String affiliation,
      String introduction,
      String profileImage,
      SnsLinks sns) {

    public static MemberResponse from(Member member) {
      return new MemberResponse(
          member.role().getDisplayName(),
          member.name(),
          member.affiliation(),
          member.introduction(),
          member.profileImageUrl(),
          SnsLinks.from(member));
    }

    public record SnsLinks(String email, String linkedin, String github, String behance) {

      public static SnsLinks from(Member member) {
        if (member.snsLinks() == null) {
          return null;
        }
        return new SnsLinks(
            member.snsLinks().email(),
            member.snsLinks().linkedin(),
            member.snsLinks().github(),
            member.snsLinks().behance());
      }
    }
  }

  public record Schedule(String name, String startDate, String endDate) {

    public static Schedule from(ActivitySchedule schedule) {
      return new Schedule(
          schedule.name(),
          schedule.startDate() != null ? schedule.startDate().toString() : null,
          schedule.endDate() != null ? schedule.endDate().toString() : null);
    }
  }
}
