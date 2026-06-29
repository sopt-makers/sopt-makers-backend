package org.sopt.makers.domain.official.faq.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.port.FaqRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

  private final FaqRepositoryPort faqRepositoryPort;

  public List<Faq> findAll() {
    return faqRepositoryPort.findAll();
  }
}
