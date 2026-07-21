package org.sopt.makers.api.controller.admin.lecture;

import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_CREATE_LECTURE;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_DELETE_LECTURE;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_END_LECTURE;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_GET_LECTURE;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_GET_LECTURES;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_GET_LECTURE_DETAIL;
import static org.sopt.makers.api.controller.admin.lecture.LectureSuccessCode.SUCCESS_START_ATTENDANCE;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.lecture.dto.LectureCreateRequest;
import org.sopt.makers.api.controller.admin.lecture.dto.LectureCreateResponse;
import org.sopt.makers.api.controller.admin.lecture.dto.LectureDetailResponse;
import org.sopt.makers.api.controller.admin.lecture.dto.LectureGetResponse;
import org.sopt.makers.api.controller.admin.lecture.dto.LecturesGetResponse;
import org.sopt.makers.api.controller.admin.lecture.dto.SubLectureStartRequest;
import org.sopt.makers.api.controller.admin.lecture.dto.SubLectureStartResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.service.LectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/lectures")
public class LectureController implements LectureApi {

  private final LectureService lectureService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createLecture(
      @RequestBody @Valid LectureCreateRequest request) {
    Lecture lecture =
        lectureService.createLecture(
            request.part(),
            request.name(),
            request.generation(),
            request.place(),
            request.startDate(),
            request.endDate(),
            request.attribute());
    return ResponseFactory.success(SUCCESS_CREATE_LECTURE, LectureCreateResponse.from(lecture));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getLectures(
      @RequestParam int generation, @RequestParam(required = false) Part part) {
    List<Lecture> lectures = lectureService.getLectures(generation, part);
    Map<Long, AttendanceStatusSummary> summaries = lectureService.getAttendanceSummaries(lectures);
    return ResponseFactory.success(
        SUCCESS_GET_LECTURES, LecturesGetResponse.from(generation, lectures, summaries));
  }

  @Override
  @GetMapping("/{lectureId}")
  public ResponseEntity<BaseResponse<?>> getLecture(@PathVariable Long lectureId) {
    Lecture lecture = lectureService.getLecture(lectureId);
    AttendanceStatusSummary summary = lectureService.getAttendanceSummary(lecture);
    return ResponseFactory.success(SUCCESS_GET_LECTURE, LectureGetResponse.from(lecture, summary));
  }

  @Override
  @PatchMapping("/attendance")
  public ResponseEntity<BaseResponse<?>> startSubLecture(
      @RequestBody @Valid SubLectureStartRequest request) {
    SubLecture subLecture =
        lectureService.startSubLecture(request.lectureId(), request.round(), request.code());
    return ResponseFactory.success(
        SUCCESS_START_ATTENDANCE, SubLectureStartResponse.from(request.lectureId(), subLecture));
  }

  @Override
  @PatchMapping("/{lectureId}")
  public ResponseEntity<BaseResponse<?>> endLecture(@PathVariable Long lectureId) {
    lectureService.endLecture(lectureId);
    return ResponseFactory.success(SUCCESS_END_LECTURE);
  }

  @Override
  @DeleteMapping("/{lectureId}")
  public ResponseEntity<BaseResponse<?>> deleteLecture(@PathVariable Long lectureId) {
    lectureService.deleteLecture(lectureId);
    return ResponseFactory.success(SUCCESS_DELETE_LECTURE);
  }

  @Override
  @GetMapping("/detail/{lectureId}")
  public ResponseEntity<BaseResponse<?>> getLectureDetail(@PathVariable Long lectureId) {
    Lecture lecture = lectureService.getLectureDetail(lectureId);
    return ResponseFactory.success(SUCCESS_GET_LECTURE_DETAIL, LectureDetailResponse.from(lecture));
  }
}
