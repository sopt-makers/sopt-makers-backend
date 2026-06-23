package org.sopt.makers.domain.official.news.service;

import static org.sopt.makers.domain.official.news.exception.NewsFailure.NOT_FOUND_NEWS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.domain.official.news.exception.NewsException;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;
import org.sopt.makers.domain.official.news.port.NewsRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

  private static final String NEWS_IMAGE_DIRECTORY = "news/";

  private final NewsRepositoryPort newsRepositoryPort;
  private final NewsFileStoragePort newsFileStoragePort;

  @Transactional
  public News createWithFile(CreateNewsWithFileCommand command) {
    String imageUrl = newsFileStoragePort.upload(command.image(), NEWS_IMAGE_DIRECTORY);
    News news = News.create(imageUrl, command.title(), command.link());
    News saved = newsRepositoryPort.save(news);
    log.info("최신소식 추가 완료 - id={}", saved.id());
    return saved;
  }

  @Transactional
  public News create(CreateNewsCommand command) {
    News news = News.create(command.imageUrl(), command.title(), command.link());
    News saved = newsRepositoryPort.save(news);
    log.info("최신소식 추가 완료 - id={}", saved.id());
    return saved;
  }

  @Transactional
  public List<News> createAll(BulkCreateNewsCommand command) {
    List<News> news =
        command.news().stream()
            .map(data -> News.create(data.imageUrl(), data.title(), data.link()))
            .toList();
    List<News> saved = newsRepositoryPort.saveAll(news);
    log.info("최신소식 일괄 생성 완료 - count={}", saved.size());
    return saved;
  }

  @Transactional
  public News edit(Integer id, EditNewsCommand command) {
    News news = getById(id);
    News saved =
        newsRepositoryPort.save(news.update(command.imageUrl(), command.title(), command.link()));
    log.info("최신소식 수정 완료 - id={}", id);
    return saved;
  }

  @Transactional
  public News editWithFile(Integer id, EditNewsWithFileCommand command) {
    News news = getById(id);
    String imageUrl = news.imageUrl();
    if (command.image() != null) {
      newsFileStoragePort.delete(news.imageUrl());
      imageUrl = newsFileStoragePort.upload(command.image(), NEWS_IMAGE_DIRECTORY);
    }
    News saved = newsRepositoryPort.save(news.update(imageUrl, command.title(), command.link()));
    log.info("최신소식 수정 완료 - id={}", id);
    return saved;
  }

  @Transactional
  public void delete(Integer id) {
    News news = getById(id);
    newsFileStoragePort.delete(news.imageUrl());
    newsRepositoryPort.delete(news);
    log.info("최신소식 삭제 완료 - id={}", id);
  }

  public News getById(Integer id) {
    return newsRepositoryPort.findById(id).orElseThrow(() -> new NewsException(NOT_FOUND_NEWS));
  }

  public List<News> findAll() {
    return newsRepositoryPort.findAll();
  }

  public NewsFileStoragePort.PresignedFile generatePresignedUrl(
      NewsFileStoragePort.PresignedFileRequest request) {
    return newsFileStoragePort.generatePresignedUrl(request);
  }

  public record CreateNewsCommand(String imageUrl, String title, String link) {}

  public record CreateNewsWithFileCommand(
      NewsFileStoragePort.UploadFile image, String title, String link) {}

  public record EditNewsCommand(String imageUrl, String title, String link) {}

  public record EditNewsWithFileCommand(
      NewsFileStoragePort.UploadFile image, String title, String link) {}

  public record BulkCreateNewsCommand(List<NewsData> news) {

    public record NewsData(String title, String link, String imageUrl) {}
  }
}
