package org.sopt.makers.api.controller.crew.meeting.dto;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.user.Activity;

@NoArgsConstructor(access = PRIVATE)
public final class MeetingResponse {

  public record Created(Long meetingId) {

    public static Created from(Meeting meeting) {
      return new Created(meeting.id());
    }
  }

  public record ApplyCreated(Long applyId) {

    public static ApplyCreated from(MeetingApply apply) {
      return new ApplyCreated(apply.id());
    }
  }

  public record Summary(
      Long id,
      Long userId,
      String title,
      String subTitle,
      String category,
      List<MeetingImage> imageURL,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      long appliedCount,
      long approvedCount,
      int status) {

    public static Summary from(MeetingService.MeetingSummary summary) {
      Meeting meeting = summary.meeting();
      return new Summary(
          meeting.id(),
          meeting.userId(),
          meeting.title(),
          meeting.subTitle(),
          meeting.category().getValue(),
          meeting.images(),
          meeting.startDate(),
          meeting.endDate(),
          meeting.capacity(),
          summary.appliedCount(),
          summary.approvedCount(),
          summary.status().getValue());
    }
  }

  public record Detail(
      Long id,
      Long userId,
      Long meetingDemandId,
      String title,
      String subTitle,
      String category,
      List<MeetingImage> imageURL,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String desc,
      String processDesc,
      LocalDateTime mStartDate,
      LocalDateTime mEndDate,
      String leaderDesc,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer createdGeneration,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts,
      User meetingCreator,
      List<User> coMeetingLeaders,
      Boolean isHost,
      Boolean apply,
      Boolean approved,
      Boolean isCoLeader,
      long approvedApplyCount,
      List<ApplyWithUser> applies) {

    public static Detail from(Meeting meeting) {
      return new Detail(
          meeting.id(),
          meeting.userId(),
          meeting.meetingDemandId(),
          meeting.title(),
          meeting.subTitle(),
          meeting.category().getValue(),
          meeting.images(),
          meeting.startDate(),
          meeting.endDate(),
          meeting.capacity(),
          meeting.description(),
          meeting.processDescription(),
          meeting.activityStartDate(),
          meeting.activityEndDate(),
          meeting.leaderDescription(),
          meeting.note(),
          meeting.isMentorNeeded(),
          meeting.canJoinOnlyActiveGeneration(),
          meeting.joinInfo(),
          meeting.createdGeneration(),
          meeting.targetActiveGeneration(),
          meeting.joinableParts(),
          null,
          List.of(),
          null,
          null,
          null,
          null,
          0,
          List.of());
    }

    public static Detail from(MeetingService.MeetingDetail detail) {
      Meeting meeting = detail.meeting();
      return new Detail(
          meeting.id(),
          meeting.userId(),
          meeting.meetingDemandId(),
          meeting.title(),
          meeting.subTitle(),
          meeting.category().getValue(),
          meeting.images(),
          meeting.startDate(),
          meeting.endDate(),
          meeting.capacity(),
          meeting.description(),
          meeting.processDescription(),
          meeting.activityStartDate(),
          meeting.activityEndDate(),
          meeting.leaderDescription(),
          meeting.note(),
          meeting.isMentorNeeded(),
          meeting.canJoinOnlyActiveGeneration(),
          meeting.joinInfo(),
          meeting.createdGeneration(),
          meeting.targetActiveGeneration(),
          meeting.joinableParts(),
          User.from(detail.leader()),
          detail.coLeaders().stream().map(User::from).toList(),
          detail.isHost(),
          detail.isApply(),
          detail.isApproved(),
          detail.isCoLeader(),
          detail.approvedApplyCount(),
          detail.applies().stream().map(ApplyWithUser::from).toList());
    }
  }

  public record PartMembers(
      String part,
      int participantCount,
      boolean isActiveGeneration,
      Integer activeGeneration,
      List<ApplyWithUser> appliedInfo) {

    public static PartMembers from(MeetingService.MeetingPartMembers members) {
      return new PartMembers(
          members.part(),
          members.participantCount(),
          members.isActiveGeneration(),
          members.activeGeneration(),
          members.appliedInfo().stream().map(ApplyWithUser::from).toList());
    }
  }

  public record Apply(
      Long id,
      Integer type,
      Long meetingId,
      Long userId,
      String content,
      LocalDateTime appliedDate,
      Integer status) {

    public static Apply from(MeetingApply apply) {
      return new Apply(
          apply.id(),
          apply.type().getValue(),
          apply.meetingId(),
          apply.userId(),
          apply.content(),
          apply.appliedDate(),
          apply.status().getValue());
    }
  }

  public record ApplyWithUser(
      Long id,
      Integer applyNumber,
      Integer type,
      Long meetingId,
      Long userId,
      String content,
      LocalDateTime appliedDate,
      Integer status,
      User user) {

    public static ApplyWithUser from(MeetingService.ApplyDetail detail) {
      MeetingApply apply = detail.apply();
      return new ApplyWithUser(
          apply.id(),
          null,
          apply.type().getValue(),
          apply.meetingId(),
          apply.userId(),
          apply.content(),
          apply.appliedDate(),
          apply.status().getValue(),
          User.from(detail.user()));
    }
  }

  public record User(
      Long userId, String name, String profileImage, Integer generation, String part) {

    public static User from(MeetingUser user) {
      if (user == null) {
        return null;
      }
      Activity activity = user.findLatestActivity().orElse(null);
      return new User(
          user.id(),
          user.name(),
          user.profileImage(),
          activity == null ? null : activity.generation(),
          activity == null || activity.part() == null ? null : activity.part().getName());
    }
  }
}
