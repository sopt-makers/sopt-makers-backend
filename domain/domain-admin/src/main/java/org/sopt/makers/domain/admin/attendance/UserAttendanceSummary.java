package org.sopt.makers.domain.admin.attendance;

import java.util.List;
import org.sopt.makers.domain.admin.user.UserActivity;

public record UserAttendanceSummary(UserActivity activity, List<AttendanceDetail> lectures) {}
