package org.sopt.makers.api.controller.official.news;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.sopt.makers.api.controller.official.news.dto.NewsRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "뉴스", description = "공식 홈페이지 뉴스 API")
public interface NewsApi {

  @Operation(summary = "뉴스 생성 (파일)")
  ResponseEntity<BaseResponse<?>> createNews(NewsRequest.CreateWithFile request) throws IOException;

  @Operation(summary = "뉴스 생성")
  ResponseEntity<BaseResponse<?>> createNewsV2(NewsRequest.Create request);

  @Operation(summary = "뉴스 수정 (파일)")
  ResponseEntity<BaseResponse<?>> editNews(Integer id, NewsRequest.EditWithFile request)
      throws IOException;

  @Operation(summary = "뉴스 수정")
  ResponseEntity<BaseResponse<?>> editNewsV2(Integer id, NewsRequest.Edit request);

  @Operation(summary = "뉴스 삭제")
  ResponseEntity<BaseResponse<?>> deleteNews(NewsRequest.Delete request);

  @Operation(summary = "뉴스 단건 조회")
  ResponseEntity<BaseResponse<?>> getNews(NewsRequest.Get request);

  @Operation(summary = "뉴스 전체 조회")
  ResponseEntity<BaseResponse<?>> getNewsList();
}
