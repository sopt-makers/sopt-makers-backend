package org.sopt.makers.api.controller.admin.user;

import static org.sopt.makers.api.controller.admin.user.AdminUserSuccessCode.SUCCESS_GET_USERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.user.dto.UsersGetResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.user.AdminUser;
import org.sopt.makers.domain.admin.user.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController implements AdminUserApi {

  private final AdminUserService adminUserService;

  @Override
  @GetMapping("/list")
  public ResponseEntity<BaseResponse<?>> getUsers(
      @RequestParam(required = false) Part part,
      @RequestParam int generation,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int limit) {
    int totalCount = adminUserService.countUsers(generation, part);
    List<AdminUser> users = adminUserService.getUsers(generation, part, page, limit);
    return ResponseFactory.success(SUCCESS_GET_USERS, UsersGetResponse.from(totalCount, users));
  }
}
