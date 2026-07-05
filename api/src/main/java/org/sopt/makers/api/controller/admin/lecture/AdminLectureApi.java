package org.sopt.makers.api.controller.admin.lecture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.admin.lecture.dto.LectureCreateRequest;
import org.sopt.makers.api.controller.admin.lecture.dto.SubLectureStartRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 세션", description = "어드민 세션 관리 API")
public interface AdminLectureApi {

  @Operation(summary = "세션 생성")
  ResponseEntity<BaseResponse<?>> createLecture(@Valid LectureCreateRequest request);

  @Operation(summary = "세션 리스트 조회")
  ResponseEntity<BaseResponse<?>> getLectures(int generation, Part part);

  @Operation(summary = "세션 단일 조회")
  ResponseEntity<BaseResponse<?>> getLecture(Long lectureId);

  @Operation(summary = "출석 시작")
  ResponseEntity<BaseResponse<?>> startSubLecture(@Valid SubLectureStartRequest request);

  @Operation(summary = "세션 종료")
  ResponseEntity<BaseResponse<?>> endLecture(Long lectureId);

  @Operation(summary = "세션 삭제")
  ResponseEntity<BaseResponse<?>> deleteLecture(Long lectureId);

  @Operation(summary = "세션 팝업용 상세 조회")
  ResponseEntity<BaseResponse<?>> getLectureDetail(Long lectureId);
}
