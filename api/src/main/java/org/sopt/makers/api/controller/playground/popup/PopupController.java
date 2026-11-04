package org.sopt.makers.api.controller.playground.popup;

import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.CREATE_POPUP;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.DELETE_POPUP;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.GET_ALL_POPUPS;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.GET_CURRENT_POPUP;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.GET_POPUP;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.UPDATE_POPUP;
import static org.sopt.makers.api.controller.playground.popup.PopupSuccessCode.VALIDATE_ADMIN_KEY;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.security.AdminKeyValidator;
import org.sopt.makers.api.controller.playground.popup.dto.PopupRequest;
import org.sopt.makers.api.controller.playground.popup.dto.PopupResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.popup.Popup;
import org.sopt.makers.domain.playground.popup.service.PopupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popups")
public class PopupController implements PopupApi {

  private final PopupService popupService;
  private final AdminKeyValidator adminKeyValidator;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createPopup(
      @RequestHeader("admin-key") String adminKey, @Valid @RequestBody PopupRequest request) {
    adminKeyValidator.validate(adminKey);
    Popup popup =
        popupService.createPopup(
            LocalDate.parse(request.startDate()),
            LocalDate.parse(request.endDate()),
            request.pcImageUrl(),
            request.mobileImageUrl(),
            request.linkUrl(),
            request.openInNewTab(),
            request.showOnlyToRecentGeneration());
    return ResponseFactory.success(CREATE_POPUP, PopupResponse.from(popup));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getAllPopups(@RequestHeader("admin-key") String adminKey) {
    adminKeyValidator.validate(adminKey);
    List<PopupResponse> responses =
        popupService.getAllPopups().stream().map(PopupResponse::from).toList();
    return ResponseFactory.success(GET_ALL_POPUPS, responses);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> getPopupById(
      @RequestHeader("admin-key") String adminKey, @PathVariable Long id) {
    adminKeyValidator.validate(adminKey);
    return ResponseFactory.success(GET_POPUP, PopupResponse.from(popupService.getPopupById(id)));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> updatePopup(
      @RequestHeader("admin-key") String adminKey,
      @PathVariable Long id,
      @Valid @RequestBody PopupRequest request) {
    adminKeyValidator.validate(adminKey);
    Popup popup =
        popupService.updatePopup(
            id,
            LocalDate.parse(request.startDate()),
            LocalDate.parse(request.endDate()),
            request.pcImageUrl(),
            request.mobileImageUrl(),
            request.linkUrl(),
            request.openInNewTab(),
            request.showOnlyToRecentGeneration());
    return ResponseFactory.success(UPDATE_POPUP, PopupResponse.from(popup));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> deletePopup(
      @RequestHeader("admin-key") String adminKey, @PathVariable Long id) {
    adminKeyValidator.validate(adminKey);
    popupService.deletePopup(id);
    return ResponseFactory.success(DELETE_POPUP);
  }

  @Override
  @PostMapping("/validate-admin-key")
  public ResponseEntity<BaseResponse<?>> validateAdminKey(
      @RequestHeader("admin-key") String adminKey) {
    adminKeyValidator.validate(adminKey);
    return ResponseFactory.success(VALIDATE_ADMIN_KEY);
  }

  @Override
  @GetMapping("/current")
  public ResponseEntity<BaseResponse<?>> getCurrentPopup() {
    Popup popup = popupService.getCurrentPopup();
    return ResponseFactory.success(
        GET_CURRENT_POPUP, popup != null ? PopupResponse.from(popup) : null);
  }
}
