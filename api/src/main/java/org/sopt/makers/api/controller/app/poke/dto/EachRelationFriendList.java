package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.poke.EachRelationFriendListData;

public record EachRelationFriendList(
    @Schema(description = "해당 관계의 친구 목록. 콕 횟수 내림차순, 같으면 유저 아이디 오름차순")
        List<SimplePokeProfile> friendList,
    @Schema(description = "해당 관계의 전체 친구 수", example = "5") int totalSize,
    @Schema(description = "전체 페이지 수", example = "1") int totalPageSize,
    @Schema(description = "한 페이지 크기", example = "25") int pageSize,
    @Schema(description = "0부터 시작하는 현재 페이지 번호", example = "0") int pageNum) {

  public static EachRelationFriendList of(EachRelationFriendListData data) {
    return new EachRelationFriendList(
        data.friendList().stream().map(SimplePokeProfile::of).toList(),
        data.totalSize(),
        data.totalPageSize(),
        data.pageSize(),
        data.pageNum());
  }
}
