package org.sopt.makers.api.controller.crew.dining;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.dining.dto.query.GetAttendeesRequest;
import org.sopt.makers.api.controller.crew.dining.dto.query.GetDiningsRequest;
import org.sopt.makers.api.controller.crew.dining.dto.request.AddAttendeeRequest;
import org.sopt.makers.api.controller.crew.dining.dto.request.CreateAndUpdateDiningRequest;
import org.sopt.makers.api.controller.crew.dining.dto.request.CreateAndUpdateRoundRequest;
import org.sopt.makers.api.controller.crew.dining.dto.response.CreateDiningResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.CreateRoundResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetApprovedPayersResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetAttendanceResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetAttendeeManagementResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetAttendeesCsvResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetAttendeesResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetDiningDetailResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetDiningsResponse;
import org.sopt.makers.api.controller.crew.dining.dto.response.GetPayerRequestsResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "회식")
public interface DiningV2Api {

  @Operation(summary = "회식 등록", description = "회식과 1차 회차를 생성하고, 개설자를 1차 참석자로 등록합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "meetingId: 10"),
        @ApiResponse(
            responseCode = "400",
            description =
                "VALIDATION_EXCEPTION, 지난 날짜로 회식을 등록할 수 없습니다., " + "네이버지도 또는 카카오맵 링크만 등록할 수 있습니다.",
            content = @Content),
      })
  ResponseEntity<CreateDiningResponse> createDining(
      CreateAndUpdateDiningRequest requestBody, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회식 수정", description = "회식 정보와 1차 회차 정보를 수정합니다. 개설자만 가능합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회식 수정 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION, 종료된 회식입니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<Void> updateDining(
      Long meetingId,
      CreateAndUpdateDiningRequest requestBody,
      @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회식 삭제",
      description = "회식과 모든 회차, 참석 기록을 삭제합니다. 마감된 회차가 있거나 종료된 회식은 삭제할 수 없습니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "회식 삭제 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "종료된 회식입니다., 마감된 회차가 있는 회식은 삭제할 수 없습니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<Void> deleteDining(Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회식 종료",
      description = "회식을 종료하고 마감하지 않은 회차를 함께 마감합니다. 종료 후에는 회차 추가와 회식·회차 수정이 불가합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회식 종료 성공"),
        @ApiResponse(responseCode = "400", description = "종료된 회식입니다.", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<Void> endDining(Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 추가",
      description = "새 회차를 추가하고 개설자를 참석자로 등록합니다. 새 회차 생성 시점에 이전 회차에 참석 중인 사용자에게 알림을 보냅니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "roundId: 2"),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION, 종료된 회식입니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<CreateRoundResponse> createRound(
      Long meetingId,
      CreateAndUpdateRoundRequest requestBody,
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "회차 수정", description = "회차의 장소, 링크, 시작 시간을 수정합니다. 마감된 회차도 수정할 수 있습니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회차 수정 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION, 종료된 회식입니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회식입니다., 존재하지 않는 회차입니다.",
            content = @Content),
      })
  ResponseEntity<Void> updateRound(
      Long meetingId,
      Long roundId,
      CreateAndUpdateRoundRequest requestBody,
      @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 삭제",
      description = "2차 이상 회차와 해당 회차의 참석 기록을 삭제합니다. 1차와 마감된 회차는 삭제할 수 없습니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "회차 삭제 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "종료된 회식입니다., 1차는 회식 삭제로만 삭제할 수 있습니다., " + "마감된 회차는 삭제할 수 없습니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회식입니다., 존재하지 않는 회차입니다.",
            content = @Content),
      })
  ResponseEntity<Void> deleteRound(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회차 마감", description = "회차를 마감(확정)합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회차 마감 성공"),
        @ApiResponse(responseCode = "400", description = "이미 마감된 회차입니다.", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회식입니다., 존재하지 않는 회차입니다.",
            content = @Content),
      })
  ResponseEntity<Void> closeRound(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회차 참석 신청", description = "마감 전 회차에 본인의 참석을 등록합니다. 기수와 파트 제한이 없습니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "참석 신청 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "존재하지 않거나 유효하지 않은 유저입니다.",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION, 이미 참석 중인 회차입니다., 이미 마감된 회차입니다.",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> applyAttendance(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회차 참석 취소", description = "마감 전 회차의 본인 참석 기록을 삭제하고 삭제 이력을 보관합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "참석 취소 성공"),
        @ApiResponse(responseCode = "400", description = "이미 마감된 회차입니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> cancelAttendance(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회차 참석 현황 조회", description = "현재 참석 인원과 본인의 참석 여부 및 해당 회차 공동 결제자 상태를 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "참석 현황 조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<GetAttendanceResponse> getAttendance(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 참석자 목록 조회",
      description = "개설자 또는 해당 회차에서 승인된 공동 결제자가 상세 정보를 조회합니다. 등록 시각 최신순이 기본이며 10/30/50명씩 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "참석자 목록 조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  @Parameter(name = "queryCommand", hidden = true)
  @Parameter(
      name = "page",
      description = "페이지, default = 1",
      example = "1",
      schema = @Schema(type = "integer", format = "int32", defaultValue = "1", minimum = "1"))
  // allowableValues는 문자열 enum으로 나가 integer 타입과 어긋나므로 허용값은 설명에만 둔다.
  @Parameter(
      name = "take",
      description = "가져올 데이터 개수. 10, 30, 50 중 하나, default = 10",
      example = "10",
      schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
  @Parameter(
      name = "date",
      description = "등록 시각 정렬 (desc: 최신순, asc: 오래된순), default = desc",
      example = "desc",
      schema =
          @Schema(
              type = "string",
              defaultValue = "desc",
              allowableValues = {"desc", "asc"}))
  ResponseEntity<GetAttendeeManagementResponse> getAttendees(
      Long meetingId,
      Long roundId,
      GetAttendeesRequest queryCommand,
      @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 전체 참석자 CSV 다운로드",
      description =
          "선택한 회차의 전체 참석자를 페이지네이션 없이 CSV로 생성하여 S3 파일 URL을 반환합니다. "
              + "개설자 또는 해당 회차에서 승인된 공동 결제자만 가능하며, 종료된 회식에서도 가능합니다. "
              + "CSV 컬럼: 번호 / 이름 / 최근 기수 / 연락처. 등록 시각 오름차순, 동일 시각에는 참석 기록 ID 오름차순으로 정렬하고 번호는 1부터 부여합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "CSV 파일 URL 반환. 참석자가 없으면 헤더만 있는 CSV"),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식 또는 해당 회식의 회차를 찾을 수 없습니다.",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "CSV 생성 또는 S3 업로드 실패", content = @Content),
      })
  ResponseEntity<GetAttendeesCsvResponse> getAttendeesCsv(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 참석자 직접 추가",
      description = "개설자 또는 해당 회차에서 승인된 공동 결제자가 추가합니다. 종료 후에도 가능합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "참석자 추가 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "존재하지 않거나 유효하지 않은 유저입니다.",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION, 이미 참석 중인 회차입니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> addAttendee(
      Long meetingId,
      Long roundId,
      AddAttendeeRequest requestBody,
      @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 참석자 삭제",
      description = "개설자 또는 해당 회차에서 승인된 공동 결제자가 삭제합니다. 개설자 삭제 및 종료 후 삭제도 가능합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "참석자 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> deleteAttendee(
      Long meetingId, Long roundId, Long attendeeUserId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회차 참석 현황 모달 목록 조회",
      description =
          "로그인한 모든 사용자가 참석자의 ID, 이름, 프로필 이미지를 조회합니다. "
              + "참석 등록 시각 오름차순, 같으면 참석 기록 ID 오름차순으로 정렬합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "참석 현황 모달 목록 조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "회식 또는 회차를 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<GetAttendeesResponse> getAttendanceAttendees(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "공동 결제자 요청",
      description = "해당 회차 참석자 본인만 요청할 수 있습니다. 개설자는 요청할 수 없습니다. 마감된 회차와 종료된 회식에서도 가능합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "공동 결제자 요청 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "이미 결제자 요청을 했습니다., 개설자는 공동 결제자를 요청할 수 없습니다.",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> requestPayer(
      Long meetingId, Long roundId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "공동 결제자 대기 요청 목록",
      description = "개설자만 회식 전체의 PENDING 요청을 요청 시각 오름차순으로 조회합니다. 회차 번호는 현재 기준입니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "대기 요청 목록 조회 성공. 요청이 없으면 빈 리스트"),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<GetPayerRequestsResponse> getPayerRequests(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "승인된 공동 결제자 목록",
      description =
          "개설자만 회식 전체의 APPROVED 공동 결제자를 요청 시각 오름차순으로 조회합니다. "
              + "동일 사용자도 승인된 회차별로 반환하며, 회차 번호는 현재 기준입니다. roundId와 userId로 기존 승인 해제 API를 호출할 수 있습니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "승인된 공동 결제자 목록 조회 성공. 공동 결제자가 없으면 빈 리스트"),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<GetApprovedPayersResponse> getApprovedPayers(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "공동 결제자 승인",
      description = "개설자만 가능합니다. 마감된 회차와 종료된 회식에서도 가능합니다. 참석 기록은 유지합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "공동 결제자 승인 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "승인 대기 중인 결제자 요청이 없습니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> approvePayer(
      Long meetingId, Long roundId, Long requesterUserId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "공동 결제자 거절",
      description = "개설자만 가능합니다. 마감된 회차와 종료된 회식에서도 가능합니다. 참석 기록은 유지합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "공동 결제자 거절 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "승인 대기 중인 결제자 요청이 없습니다.",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> rejectPayer(
      Long meetingId, Long roundId, Long requesterUserId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "공동 결제자 승인 해제",
      description = "개설자만 가능합니다. 마감된 회차와 종료된 회식에서도 가능합니다. 참석 기록은 유지합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "공동 결제자 승인 해제 성공"),
        @ApiResponse(responseCode = "400", description = "승인된 공동 결제자가 아닙니다.", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "회식, 회차 또는 참석 기록을 찾을 수 없습니다.",
            content = @Content),
      })
  ResponseEntity<Void> revokePayer(
      Long meetingId, Long roundId, Long requesterUserId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "회식 메인 목록 조회",
      description = "전체 회식 또는 내 회식(개설했거나 참석 중인 회식)을 조회합니다. " + "정렬은 진행 중 우선, 회식 일자 최신순, 등록 최신순입니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회식 목록 조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "VALIDATION_EXCEPTION",
            content = @Content),
      })
  @Parameter(name = "queryCommand", hidden = true)
  @Parameter(
      name = "page",
      description = "페이지, default = 1",
      example = "1",
      schema = @Schema(type = "integer", format = "int32", defaultValue = "1", minimum = "1"))
  @Parameter(
      name = "take",
      description = "가져올 데이터 개수, default = 12",
      example = "12",
      schema =
          @Schema(
              type = "integer",
              format = "int32",
              defaultValue = "12",
              minimum = "1",
              maximum = "50"))
  @Parameter(
      name = "tab",
      description = "목록 탭. ALL: 전체 회식, MY: 내 회식(개설했거나 참석 중인 회식), default = ALL",
      example = "ALL",
      schema =
          @Schema(
              type = "string",
              defaultValue = "ALL",
              allowableValues = {"ALL", "MY"}))
  @Parameter(
      name = "status",
      description = "진행 상태. 생략하면 전체 상태",
      example = "IN_PROGRESS",
      schema =
          @Schema(
              type = "string",
              allowableValues = {"IN_PROGRESS", "ENDED"}))
  @Parameter(
      name = "part",
      description = "파트. 생략하면 전체 파트이며, 전체 파트 회식은 모든 파트 필터에 포함된다",
      example = "SERVER",
      schema =
          @Schema(
              type = "string",
              allowableValues = {"PM", "DESIGN", "IOS", "ANDROID", "SERVER", "WEB"}))
  @Parameter(
      name = "query",
      description = "회식명 검색어 (최대 30자)",
      example = "서버",
      schema = @Schema(type = "string", maxLength = 30))
  ResponseEntity<GetDiningsResponse> getDinings(
      GetDiningsRequest queryCommand, @Parameter(hidden = true) Long userId);

  @Operation(summary = "회식 상세 조회", description = "회식 정보와 회차 목록, 조회자의 회차별 참석 상태를 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회식 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회식입니다.", content = @Content),
      })
  ResponseEntity<GetDiningDetailResponse> getDiningDetail(
      Long meetingId, @Parameter(hidden = true) Long userId);
}
