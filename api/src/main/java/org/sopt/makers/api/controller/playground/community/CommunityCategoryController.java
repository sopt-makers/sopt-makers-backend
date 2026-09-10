package org.sopt.makers.api.controller.playground.community;

import static org.sopt.makers.api.controller.playground.community.CommunityCategorySuccessCode.GET_ALL_CATEGORIES;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.playground.community.dto.CommunityCategoryResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.community.service.CategoryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/category")
public class CommunityCategoryController implements CommunityCategoryApi {

  private final CategoryQueryService categoryQueryService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getAllCategories() {
    return ResponseFactory.success(
        GET_ALL_CATEGORIES,
        CommunityCategoryResponse.listFrom(
            categoryQueryService.findAllActiveCategoriesWithParent()));
  }
}
