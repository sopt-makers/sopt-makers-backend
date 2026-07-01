package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.port.FaqRepositoryPort;
import org.sopt.makers.storage.db.official.entity.FaqEntity;
import org.sopt.makers.storage.db.official.repository.FaqJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqRepositoryAdapter implements FaqRepositoryPort {

  private final FaqJpaRepository faqJpaRepository;

  @Override
  public List<Faq> findAll() {
    return faqJpaRepository.findAllOrderByPart().stream().map(FaqEntity::toDomain).toList();
  }

  @Transactional
  @Override
  public List<Faq> saveAll(List<Faq> faqs) {
    List<FaqEntity> entities = faqs.stream().map(FaqEntity::fromDomain).toList();
    return faqJpaRepository.saveAll(entities).stream().map(FaqEntity::toDomain).toList();
  }

  @Transactional
  @Override
  public void deleteAll() {
    faqJpaRepository.deleteAll();
  }
}
