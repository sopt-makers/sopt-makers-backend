package org.sopt.makers.api.controller.admin.member.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.member.Member;

public record MembersGetResponse(List<MemberItem> members, int totalCount) {
  public static MembersGetResponse from(int totalCount, List<Member> members) {
    return new MembersGetResponse(members.stream().map(MemberItem::from).toList(), totalCount);
  }

  public record MemberItem(Long id, String name, Part part, float score, AttendanceTotal total) {
    public static MemberItem from(Member member) {
      return new MemberItem(
          member.id(),
          member.name(),
          member.part(),
          member.attendanceScore(),
          new AttendanceTotal(
              member.attendanceCount(),
              member.absentCount(),
              member.tardyCount(),
              member.participateCount()));
    }
  }

  public record AttendanceTotal(int attendance, int absent, int tardy, int participate) {}
}
