package org.sopt.makers.api.controller.playground.coffeechat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatDetailsRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatOpenRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatRequest;
import org.sopt.makers.api.controller.playground.coffeechat.dto.CoffeeChatReviewRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "커피챗 API", description = "커피챗과 관련된 API들")
@SecurityRequirement(name = "Authorization")
public interface CoffeeChatApi {

  @Operation(summary = "랜덤 커피챗 목록 조회")
  ResponseEntity<BaseResponse<?>> getRandomCoffeeChatList(Long userId);

  @Operation(summary = "최근 커피챗 목록 조회")
  ResponseEntity<BaseResponse<?>> getRecentCoffeeChatList();

  @Operation(summary = "커피챗 검색 목록 조회")
  ResponseEntity<BaseResponse<?>> getSearchCoffeeChatList(
      Long userId, String section, String topicType, String career, String part, String search);

  @Operation(summary = "커피챗 상세 조회")
  ResponseEntity<BaseResponse<?>> getCoffeeChatDetail(Long userId, Long memberId);

  @Operation(summary = "커피챗 신청")
  ResponseEntity<BaseResponse<?>> sendCoffeeChatRequest(Long userId, CoffeeChatRequest request);

  @Operation(summary = "커피챗 활성화 여부 조회")
  ResponseEntity<BaseResponse<?>> getCoffeeChatActivate(Long userId);

  @Operation(summary = "커피챗 오픈 여부 변경")
  ResponseEntity<BaseResponse<?>> updateCoffeeChatOpen(Long userId, CoffeeChatOpenRequest request);

  @Operation(summary = "커피챗 히스토리 조회")
  ResponseEntity<BaseResponse<?>> getCoffeeChatHistories(Long userId);

  @Operation(summary = "커피챗 정보 등록")
  ResponseEntity<BaseResponse<?>> createCoffeeChatDetails(Long userId, CoffeeChatDetailsRequest request);

  @Operation(summary = "커피챗 정보 수정")
  ResponseEntity<BaseResponse<?>> updateCoffeeChatDetails(Long userId, CoffeeChatDetailsRequest request);

  @Operation(summary = "커피챗 정보 삭제")
  ResponseEntity<BaseResponse<?>> deleteCoffeeChatDetails(Long userId);

  @Operation(summary = "커피챗 리뷰 등록")
  ResponseEntity<BaseResponse<?>> createCoffeeChatReview(Long userId, CoffeeChatReviewRequest request);

  @Operation(summary = "최근 커피챗 리뷰 조회")
  ResponseEntity<BaseResponse<?>> getRecentCoffeeChatReviews();
}
