package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;
import org.springframework.data.domain.Page;

public record ClapUserListResponse(
    @Schema(description = "박수를 친 유저 목록") List<ClapUserProfileResponse> users,
    @Schema(description = "전체 페이지 수", example = "3") int totalPageSize,
    @Schema(description = "한 페이지 크기", example = "25") Integer pageSize,
    @Schema(description = "0부터 시작하는 현재 페이지 번호", example = "0") Integer pageNum) {

  public static ClapUserListResponse of(Page<ClapUserProfile> page) {
    return new ClapUserListResponse(
        page.getContent().stream().map(ClapUserProfileResponse::of).toList(),
        page.getTotalPages(),
        page.getSize(),
        page.getNumber());
  }
}
