package org.sopt.makers.api.controller.playground.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundCrewRelationPort.CrewMeetingItem;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundCrewRelationPort.CrewMeetingPage;

public record MemberCrewResponse(List<MemberCrewVo> meetings, PaginationMeta meta) {

  public record MemberCrewVo(
      Long id,
      Boolean isMeetingLeader,
      String title,
      String imageUrl,
      String category,
      Boolean isActiveMeeting,
      LocalDateTime mstartDate,
      LocalDateTime mendDate) {}

  public record PaginationMeta(
      Integer page, Integer take, Integer itemCount, Integer pageCount, Boolean hasPreviousPage, Boolean hasNextPage) {}

  public static MemberCrewResponse from(CrewMeetingPage page) {
    List<MemberCrewVo> meetings = page.meetings().stream().map(MemberCrewResponse::toVo).toList();
    PaginationMeta meta =
        new PaginationMeta(
            page.page(),
            page.limit(),
            (int) page.totalElements(),
            page.totalPages(),
            page.hasPrevious(),
            page.hasNext());
    return new MemberCrewResponse(meetings, meta);
  }

  private static MemberCrewVo toVo(CrewMeetingItem item) {
    return new MemberCrewVo(
        item.id(),
        item.isMeetingLeader(),
        item.title(),
        item.imageUrl(),
        item.category(),
        item.isActiveMeeting(),
        item.startDate(),
        item.endDate());
  }
}
