package org.sopt.makers.api.controller.crew.dining;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.resolver.CurrentUserId;
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
import org.sopt.makers.domain.crew.dining.service.DiningV2Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dining/v2")
@RequiredArgsConstructor
public class DiningV2Controller implements DiningV2Api {

  private final DiningV2Service diningV2Service;

  @Override
  @PostMapping
  public ResponseEntity<CreateDiningResponse> createDining(
      @Valid @RequestBody CreateAndUpdateDiningRequest requestBody, @CurrentUserId Long userId) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            CreateDiningResponse.from(
                diningV2Service.createDining(requestBody.toCommand(), userId)));
  }

  @Override
  @PutMapping("/{meetingId}")
  public ResponseEntity<Void> updateDining(
      @PathVariable Long meetingId,
      @Valid @RequestBody CreateAndUpdateDiningRequest requestBody,
      @CurrentUserId Long userId) {

    diningV2Service.updateDining(meetingId, requestBody.toCommand(), userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @DeleteMapping("/{meetingId}")
  public ResponseEntity<Void> deleteDining(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {

    diningV2Service.deleteDining(meetingId, userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/{meetingId}/end")
  public ResponseEntity<Void> endDining(@PathVariable Long meetingId, @CurrentUserId Long userId) {

    diningV2Service.endDining(meetingId, userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/{meetingId}/rounds")
  public ResponseEntity<CreateRoundResponse> createRound(
      @PathVariable Long meetingId,
      @Valid @RequestBody CreateAndUpdateRoundRequest requestBody,
      @CurrentUserId Long userId) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            CreateRoundResponse.from(
                diningV2Service.createRound(meetingId, requestBody.toCommand(), userId)));
  }

  @Override
  @PutMapping("/{meetingId}/rounds/{roundId}")
  public ResponseEntity<Void> updateRound(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @Valid @RequestBody CreateAndUpdateRoundRequest requestBody,
      @CurrentUserId Long userId) {

    diningV2Service.updateRound(meetingId, roundId, requestBody.toCommand(), userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @DeleteMapping("/{meetingId}/rounds/{roundId}")
  public ResponseEntity<Void> deleteRound(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {

    diningV2Service.deleteRound(meetingId, roundId, userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/close")
  public ResponseEntity<Void> closeRound(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {

    diningV2Service.closeRound(meetingId, roundId, userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/attendance")
  public ResponseEntity<Void> applyAttendance(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    diningV2Service.applyAttendance(meetingId, roundId, userId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  @DeleteMapping("/{meetingId}/rounds/{roundId}/attendance")
  public ResponseEntity<Void> cancelAttendance(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    diningV2Service.cancelAttendance(meetingId, roundId, userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping("/{meetingId}/rounds/{roundId}/attendance")
  public ResponseEntity<GetAttendanceResponse> getAttendance(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetAttendanceResponse.from(diningV2Service.getAttendance(meetingId, roundId, userId)));
  }

  @Override
  @GetMapping("/{meetingId}/rounds/{roundId}/attendees")
  public ResponseEntity<GetAttendeeManagementResponse> getAttendees(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @Valid @ModelAttribute GetAttendeesRequest queryCommand,
      @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetAttendeeManagementResponse.from(
            diningV2Service.getAttendees(meetingId, roundId, queryCommand.toQuery(), userId)));
  }

  @Override
  @GetMapping("/{meetingId}/rounds/{roundId}/attendees/csv")
  public ResponseEntity<GetAttendeesCsvResponse> getAttendeesCsv(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetAttendeesCsvResponse.from(diningV2Service.getAttendeesCsv(meetingId, roundId, userId)));
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/attendees")
  public ResponseEntity<Void> addAttendee(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @Valid @RequestBody AddAttendeeRequest requestBody,
      @CurrentUserId Long userId) {
    diningV2Service.addAttendee(meetingId, roundId, requestBody.userId(), userId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  @DeleteMapping("/{meetingId}/rounds/{roundId}/attendees/{attendeeUserId}")
  public ResponseEntity<Void> deleteAttendee(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @PathVariable Long attendeeUserId,
      @CurrentUserId Long userId) {
    diningV2Service.deleteAttendee(meetingId, roundId, attendeeUserId, userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping("/{meetingId}/rounds/{roundId}/attendance/attendees")
  public ResponseEntity<GetAttendeesResponse> getAttendanceAttendees(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetAttendeesResponse.from(
            diningV2Service.getAttendanceAttendees(meetingId, roundId, userId)));
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/payer-requests")
  public ResponseEntity<Void> requestPayer(
      @PathVariable Long meetingId, @PathVariable Long roundId, @CurrentUserId Long userId) {
    diningV2Service.requestPayer(meetingId, roundId, userId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  @GetMapping("/{meetingId}/payer-requests")
  public ResponseEntity<GetPayerRequestsResponse> getPayerRequests(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetPayerRequestsResponse.from(diningV2Service.getPayerRequests(meetingId, userId)));
  }

  @Override
  @GetMapping("/{meetingId}/payers")
  public ResponseEntity<GetApprovedPayersResponse> getApprovedPayers(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseEntity.ok(
        GetApprovedPayersResponse.from(diningV2Service.getApprovedPayers(meetingId, userId)));
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/payer-requests/{requesterUserId}/approve")
  public ResponseEntity<Void> approvePayer(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @PathVariable Long requesterUserId,
      @CurrentUserId Long userId) {
    diningV2Service.approvePayer(meetingId, roundId, requesterUserId, userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/payer-requests/{requesterUserId}/reject")
  public ResponseEntity<Void> rejectPayer(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @PathVariable Long requesterUserId,
      @CurrentUserId Long userId) {
    diningV2Service.rejectPayer(meetingId, roundId, requesterUserId, userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/{meetingId}/rounds/{roundId}/payer-requests/{requesterUserId}/revoke")
  public ResponseEntity<Void> revokePayer(
      @PathVariable Long meetingId,
      @PathVariable Long roundId,
      @PathVariable Long requesterUserId,
      @CurrentUserId Long userId) {
    diningV2Service.revokePayer(meetingId, roundId, requesterUserId, userId);
    return ResponseEntity.ok().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<GetDiningsResponse> getDinings(
      @Valid @ModelAttribute GetDiningsRequest queryCommand, @CurrentUserId Long userId) {

    return ResponseEntity.ok(
        GetDiningsResponse.from(diningV2Service.getDinings(queryCommand.toQuery(), userId)));
  }

  @Override
  @GetMapping("/{meetingId}")
  public ResponseEntity<GetDiningDetailResponse> getDiningDetail(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {

    return ResponseEntity.ok(
        GetDiningDetailResponse.from(diningV2Service.getDiningDetail(meetingId, userId)));
  }
}
