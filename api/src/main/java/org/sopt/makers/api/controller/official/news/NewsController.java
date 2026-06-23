package org.sopt.makers.api.controller.official.news;

import static org.sopt.makers.api.controller.official.news.NewsSuccessCode.CREATE_NEWS;
import static org.sopt.makers.api.controller.official.news.NewsSuccessCode.DELETE_NEWS;
import static org.sopt.makers.api.controller.official.news.NewsSuccessCode.GET_NEWS;
import static org.sopt.makers.api.controller.official.news.NewsSuccessCode.GET_NEWS_LIST;
import static org.sopt.makers.api.controller.official.news.NewsSuccessCode.UPDATE_NEWS;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.news.dto.NewsRequest;
import org.sopt.makers.api.controller.official.news.dto.NewsResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
@Validated
public class NewsController {

  private final NewsService newsService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<?>> createNews(
      @Valid @ModelAttribute NewsRequest.CreateWithFile request) throws IOException {
    News result = newsService.createWithFile(request.toCommand());
    return ResponseFactory.success(CREATE_NEWS, NewsResponse.of(result));
  }

  @PostMapping("/v2")
  public ResponseEntity<BaseResponse<?>> createNewsV2(
      @Valid @RequestBody NewsRequest.Create request) {
    News result = newsService.create(request.toCommand());
    return ResponseFactory.success(CREATE_NEWS, NewsResponse.of(result));
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<?>> editNews(
      @PathVariable Integer id, @Valid @ModelAttribute NewsRequest.EditWithFile request)
      throws IOException {
    News result = newsService.editWithFile(id, request.toCommand());
    return ResponseFactory.success(UPDATE_NEWS, NewsResponse.of(result));
  }

  @PatchMapping("/{id}/v2")
  public ResponseEntity<BaseResponse<?>> editNewsV2(
      @PathVariable Integer id, @Valid @RequestBody NewsRequest.Edit request) {
    News result = newsService.edit(id, request.toCommand());
    return ResponseFactory.success(UPDATE_NEWS, NewsResponse.of(result));
  }

  @PostMapping("/delete")
  public ResponseEntity<BaseResponse<?>> deleteNews(
      @Valid @RequestBody NewsRequest.Delete request) {
    newsService.delete(request.id());
    return ResponseFactory.success(DELETE_NEWS);
  }

  @GetMapping("/news")
  public ResponseEntity<BaseResponse<?>> getNews(@Valid @ModelAttribute NewsRequest.Get request) {
    News result = newsService.getById(request.id());
    return ResponseFactory.success(GET_NEWS, NewsResponse.of(result));
  }

  @GetMapping
  public ResponseEntity<BaseResponse<?>> getNewsList() {
    List<News> result = newsService.findAll();
    return ResponseFactory.success(GET_NEWS_LIST, NewsResponse.of(result));
  }
}
