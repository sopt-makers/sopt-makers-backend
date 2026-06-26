package org.sopt.makers.domain.official.soptstory.service;

import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.ALREADY_LIKED;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.DUPLICATE_SOPT_STORY_URL;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.NOT_FOUND_SOPT_STORY;
import static org.sopt.makers.domain.official.soptstory.exception.SoptStoryFailure.NOT_LIKED;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStoryLike;
import org.sopt.makers.domain.official.soptstory.SoptStorySortType;
import org.sopt.makers.domain.official.soptstory.exception.SoptStoryException;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryLikeRepositoryPort;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryRepositoryPort;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryScraperPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptStoryService {

  private final SoptStoryRepositoryPort soptStoryRepositoryPort;
  private final SoptStoryLikeRepositoryPort soptStoryLikeRepositoryPort;
  private final SoptStoryScraperPort soptStoryScraperPort;

  @Transactional
  public ScrapedArticle createSoptStory(String articleUrl) {
    ScrapedArticle article = soptStoryScraperPort.scrap(articleUrl);
    createSoptStory(
        article.title(), article.description(), article.thumbnailUrl(), article.articleUrl());
    return article;
  }

  @Transactional
  public Long createSoptStory(String title, String description, String thumbnailUrl, String url) {
    if (soptStoryRepositoryPort.existsByUrl(url)) {
      throw new SoptStoryException(DUPLICATE_SOPT_STORY_URL);
    }

    SoptStory soptStory = SoptStory.create(title, description, thumbnailUrl, url);
    return soptStoryRepositoryPort.save(soptStory).id();
  }

  @Transactional
  public Long like(Long soptStoryId, String ip) {
    SoptStory soptStory = getById(soptStoryId);

    if (soptStoryLikeRepositoryPort.existsBySoptStoryIdAndIp(soptStoryId, ip)) {
      throw new SoptStoryException(ALREADY_LIKED);
    }

    SoptStoryLike like = SoptStoryLike.create(soptStoryId, ip);
    soptStoryRepositoryPort.save(soptStory.incrementLike());
    return soptStoryLikeRepositoryPort.save(like).id();
  }

  @Transactional
  public Long unlike(Long soptStoryId, String ip) {
    SoptStory soptStory = getById(soptStoryId);
    SoptStoryLike like =
        soptStoryLikeRepositoryPort
            .findBySoptStoryIdAndIp(soptStoryId, ip)
            .orElseThrow(() -> new SoptStoryException(NOT_LIKED));

    soptStoryRepositoryPort.save(soptStory.decrementLike());
    soptStoryLikeRepositoryPort.delete(like);
    return like.id();
  }

  public Page<SoptStory> getSoptStoryList(String sort, int pageNo, int limit) {
    Pageable pageable = PageRequest.of(pageNo - 1, limit);
    return soptStoryRepositoryPort.findAll(SoptStorySortType.from(sort), pageable);
  }

  public Set<Long> getLikedSoptStoryIds(String ip, List<SoptStory> soptStories) {
    if (soptStories.isEmpty()) {
      return Set.of();
    }

    return soptStoryLikeRepositoryPort.findAllByIpAndSoptStoryIn(ip, soptStories).stream()
        .map(SoptStoryLike::soptStoryId)
        .collect(Collectors.toSet());
  }

  private SoptStory getById(Long id) {
    return soptStoryRepositoryPort
        .findById(id)
        .orElseThrow(() -> new SoptStoryException(NOT_FOUND_SOPT_STORY));
  }
}
