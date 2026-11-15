package org.sopt.makers.domain.admin.attendance;

import java.util.List;
import org.sopt.makers.domain.admin.member.Member;

public record UserAttendanceSummary(Member member, List<MemberAttendanceDetail> lectures) {}
