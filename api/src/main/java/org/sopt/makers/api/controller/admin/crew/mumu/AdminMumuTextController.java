package org.sopt.makers.api.controller.admin.crew.mumu;

import static org.sopt.makers.api.controller.admin.crew.mumu.AdminMumuTextSuccessCode.CREATE_MUMU_TEXT;
import static org.sopt.makers.api.controller.admin.crew.mumu.AdminMumuTextSuccessCode.DELETE_MUMU_TEXT;
import static org.sopt.makers.api.controller.admin.crew.mumu.AdminMumuTextSuccessCode.GET_MUMU_TEXTS;
import static org.sopt.makers.api.controller.admin.crew.mumu.AdminMumuTextSuccessCode.UPDATE_MUMU_TEXT;

import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.crew.mumu.dto.MumuTextResponse;
import org.sopt.makers.api.controller.admin.crew.mumu.dto.MumuTextUpsertRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.post.mumu.MumuText;
import org.sopt.makers.domain.playground.post.service.MumuTextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/crew/mumu-text")
@RequiredArgsConstructor
public class AdminMumuTextController implements AdminMumuTextApi {

  private final MumuTextService mumuTextService;
  private final Clock clock;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getMumuTexts() {
    LocalDateTime now = LocalDateTime.now(clock);
    return ResponseFactory.success(
        GET_MUMU_TEXTS,
        mumuTextService.findAll().stream().map(text -> MumuTextResponse.from(text, now)).toList());
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createMumuText(
      @Valid @RequestBody MumuTextUpsertRequest request) {
    MumuText text = mumuTextService.create(request.toCommand());
    return ResponseFactory.success(
        CREATE_MUMU_TEXT, MumuTextResponse.from(text, LocalDateTime.now(clock)));
  }

  @Override
  @PatchMapping("/{mumuTextId}")
  public ResponseEntity<BaseResponse<?>> updateMumuText(
      @PathVariable Long mumuTextId, @Valid @RequestBody MumuTextUpsertRequest request) {
    MumuText text = mumuTextService.update(mumuTextId, request.toCommand());
    return ResponseFactory.success(
        UPDATE_MUMU_TEXT, MumuTextResponse.from(text, LocalDateTime.now(clock)));
  }

  @Override
  @DeleteMapping("/{mumuTextId}")
  public ResponseEntity<BaseResponse<?>> deleteMumuText(@PathVariable Long mumuTextId) {
    mumuTextService.delete(mumuTextId);
    return ResponseFactory.success(DELETE_MUMU_TEXT);
  }
}
