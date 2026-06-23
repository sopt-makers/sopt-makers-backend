package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.homepage.news.News;
import org.sopt.makers.domain.official.homepage.news.port.NewsRepositoryPort;
import org.sopt.makers.storage.db.official.entity.NewsEntity;
import org.sopt.makers.storage.db.official.repository.NewsJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsRepositoryAdapter implements NewsRepositoryPort {

  private final NewsJpaRepository newsJpaRepository;

  @Transactional
  @Override
  public News save(News news) {
    return newsJpaRepository.save(NewsEntity.fromDomain(news)).toDomain();
  }

  @Transactional
  @Override
  public List<News> saveAll(List<News> news) {
    List<NewsEntity> entities = news.stream().map(NewsEntity::fromDomain).toList();
    return newsJpaRepository.saveAll(entities).stream().map(NewsEntity::toDomain).toList();
  }

  @Override
  public Optional<News> findById(Integer id) {
    return newsJpaRepository.findById(id).map(NewsEntity::toDomain);
  }

  @Override
  public List<News> findAll() {
    return newsJpaRepository.findAll().stream().map(NewsEntity::toDomain).toList();
  }

  @Transactional
  @Override
  public void delete(News news) {
    newsJpaRepository.delete(NewsEntity.fromDomain(news));
  }
}
