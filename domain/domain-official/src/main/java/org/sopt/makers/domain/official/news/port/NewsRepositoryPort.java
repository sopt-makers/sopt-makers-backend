package org.sopt.makers.domain.official.news.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.official.news.News;

public interface NewsRepositoryPort {

  News save(News news);

  List<News> saveAll(List<News> news);

  Optional<News> findById(Integer id);

  List<News> findAll();

  void delete(News news);
}
