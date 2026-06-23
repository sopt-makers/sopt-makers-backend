package org.sopt.makers.storage.db.official.repository;

import org.sopt.makers.storage.db.official.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsJpaRepository extends JpaRepository<NewsEntity, Integer> {}
