package org.sopt.makers.api.controller.user;

import static org.sopt.makers.api.common.security.SecurityConstant.API_KEY_HEADER;
import static org.sopt.makers.api.common.security.SecurityConstant.SERVICE_NAME_HEADER;
import static org.sopt.makers.api.controller.user.UserSuccessCode.GET_USER_COUNT;
import static org.sopt.makers.api.controller.user.UserSuccessCode.GET_USER_PROFILE;
import static org.sopt.makers.api.controller.user.UserSuccessCode.UPDATE_USER_PROFILE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.user.dto.UserRequest;
import org.sopt.makers.api.controller.user.dto.UserResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private static final int DEFAULT_OFFSET = 0;
  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 200;

  private final UserQueryService userQueryService;
  private final AuthFacade authFacade;

  @GetMapping
  public ResponseEntity<BaseResponse<?>> getUserProfile(
      @RequestHeader(API_KEY_HEADER) String apiKey,
      @RequestHeader(SERVICE_NAME_HEADER) String serviceName,
      @RequestParam List<Long> userIds) {
    List<User> users = userQueryService.getUsers(userIds);
    return ResponseFactory.success(GET_USER_PROFILE, UserResponse.UserProfileAndActivity.of(users));
  }

  @PostMapping
  public ResponseEntity<BaseResponse<?>> getUserProfileWithBody(
      @RequestHeader(API_KEY_HEADER) String apiKey,
      @RequestHeader(SERVICE_NAME_HEADER) String serviceName,
      @RequestBody UserRequest.GetUserProfileByIds request) {
    List<User> users = userQueryService.getUsers(request.userIds());
    return ResponseFactory.success(GET_USER_PROFILE, UserResponse.UserProfileAndActivity.of(users));
  }

  @PutMapping("/{userId}")
  public ResponseEntity<BaseResponse<?>> updateUserProfile(
      @RequestHeader(API_KEY_HEADER) String apiKey,
      @RequestHeader(SERVICE_NAME_HEADER) String serviceName,
      @PathVariable Long userId,
      @Valid @RequestBody UserRequest.UserProfileInfo request) {
    authFacade.updateProfile(userId, request.toCommand(userId));
    return ResponseFactory.success(UPDATE_USER_PROFILE);
  }

  @GetMapping("/search")
  public ResponseEntity<BaseResponse<?>> getUserProfileByFilters(
      @RequestHeader(API_KEY_HEADER) String apiKey,
      @RequestHeader(SERVICE_NAME_HEADER) String serviceName,
      @RequestParam(required = false) Integer generation,
      @RequestParam(required = false) Part part,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Team team,
      @RequestParam(required = false) Boolean isAdmin,
      @RequestParam(defaultValue = "0") @Min(0) int offset,
      @RequestParam(defaultValue = "20") @Positive @Max(MAX_LIMIT) int limit,
      @RequestParam(defaultValue = "LATEST_REGISTERED") UserSortType orderBy) {
    UserSearchCondition condition = new UserSearchCondition(generation, part, name, team, isAdmin);
    Page<User> page =
        userQueryService.getUsersByCondition(
            condition, PageRequest.of(offset / limit, limit), orderBy);
    return ResponseFactory.success(
        GET_USER_PROFILE,
        UserResponse.PaginatedUserProfiles.of(
            page.getContent(), page.hasNext(), page.getTotalElements()));
  }

  @GetMapping("/count")
  public ResponseEntity<BaseResponse<?>> getUserCountByGeneration(
      @RequestHeader(API_KEY_HEADER) String apiKey,
      @RequestHeader(SERVICE_NAME_HEADER) String serviceName,
      @RequestParam int generation) {
    int count = userQueryService.countByGenerationAndIsSopt(generation, true);
    return ResponseFactory.success(GET_USER_COUNT, new UserResponse.UserCountByGeneration(count));
  }
}
