package org.sopt.makers.api.controller.admin.lecture.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.SubLecture;

public record LectureGetResponse(
    Long lectureId,
    String name,
    int generation,
    Part part,
    LectureAttribute attribute,
    List<SubLectureVo> subLectures,
    AttendanceStatusSummary attendances,
    LectureStatus status) {

  public static LectureGetResponse from(Lecture lecture, AttendanceStatusSummary summary) {
    return new LectureGetResponse(
        lecture.id(),
        lecture.name(),
        lecture.generation(),
        lecture.part(),
        lecture.attribute(),
        lecture.subLectures().stream().map(SubLectureVo::from).toList(),
        summary,
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
