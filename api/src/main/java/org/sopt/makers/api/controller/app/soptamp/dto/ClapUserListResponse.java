package org.sopt.makers.api.controller.app.soptamp.dto;

import java.util.List;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;
import org.springframework.data.domain.Page;

public record ClapUserListResponse(
    List<ClapUserProfileResponse> users, int totalPageSize, Integer pageSize, Integer pageNum) {

  public static ClapUserListResponse of(Page<ClapUserProfile> page) {
    return new ClapUserListResponse(
        page.getContent().stream().map(ClapUserProfileResponse::of).toList(),
        page.getTotalPages(),
        page.getSize(),
        page.getNumber());
  }
}
