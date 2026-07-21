package org.sopt.makers.api.controller.admin.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 유저", description = "어드민 유저 관리 API")
public interface AdminUserApi {

  @Operation(summary = "유저 목록 조회")
  ResponseEntity<BaseResponse<?>> getUsers(Part part, int generation, int page, int limit);
}
