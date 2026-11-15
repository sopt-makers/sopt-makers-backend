package org.sopt.makers.api.controller.admin.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 회원", description = "어드민 회원 관리 API")
public interface MemberApi {

  @Operation(summary = "회원 목록 조회")
  ResponseEntity<BaseResponse<?>> getMembers(Part part, int generation, Pageable pageable);
}
