package org.sopt.makers.api.controller.app.poke.dto;

import java.util.List;
import org.sopt.makers.domain.app.poke.EachRelationFriendListData;

public record EachRelationFriendList(
    List<SimplePokeProfile> friendList,
    int totalSize,
    int totalPageSize,
    int pageSize,
    int pageNum) {

  public static EachRelationFriendList of(EachRelationFriendListData data) {
    return new EachRelationFriendList(
        data.friendList().stream().map(SimplePokeProfile::of).toList(),
        data.totalSize(),
        data.totalPageSize(),
        data.pageSize(),
        data.pageNum());
  }
}
