package org.sopt.makers.api.controller.admin.lecture.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AdminLecture;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubLecture;
import org.sopt.makers.domain.admin.attendance.service.AdminLectureService;

public record LectureGetResponse(
    Long lectureId,
    String name,
    int generation,
    Part part,
    LectureAttribute attribute,
    List<SubLectureVo> subLectures,
    AttendanceStatusSummaryVo attendances,
    LectureStatus status) {

  public static LectureGetResponse from(AdminLecture lecture, AdminLectureService service) {
    return new LectureGetResponse(
        lecture.id(),
        lecture.name(),
        lecture.generation(),
        lecture.part(),
        lecture.attribute(),
        lecture.subLectures().stream().map(SubLectureVo::from).toList(),
        AttendanceStatusSummaryVo.from(lecture, service),
        lecture.status());
  }

  public record SubLectureVo(Long subLectureId, int round, String startAt, String code) {

    public static SubLectureVo from(SubLecture subLecture) {
      return new SubLectureVo(
          subLecture.id(),
          subLecture.round(),
          subLecture.startAt() != null ? subLecture.startAt().toString() : null,
          subLecture.code());
    }
  }
}
