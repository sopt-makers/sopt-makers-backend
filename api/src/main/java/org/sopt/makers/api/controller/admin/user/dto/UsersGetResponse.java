package org.sopt.makers.api.controller.admin.user.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.user.AdminUser;

public record UsersGetResponse(int totalCount, List<UserItem> users) {

  public static UsersGetResponse from(int totalCount, List<AdminUser> users) {
    return new UsersGetResponse(totalCount, users.stream().map(UserItem::from).toList());
  }

  public record UserItem(
      Long id,
      String name,
      Part part,
      float attendanceScore,
      int attendanceCount,
      int absentCount,
      int tardyCount,
      int participateCount) {

    public static UserItem from(AdminUser user) {
      return new UserItem(
          user.id(),
          user.name(),
          user.part(),
          user.attendanceScore(),
          user.attendanceCount(),
          user.absentCount(),
          user.tardyCount(),
          user.participateCount());
    }
  }
}
