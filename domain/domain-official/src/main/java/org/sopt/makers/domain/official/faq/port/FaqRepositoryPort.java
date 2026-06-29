package org.sopt.makers.domain.official.faq.port;

import java.util.List;
import org.sopt.makers.domain.official.faq.Faq;

public interface FaqRepositoryPort {

  List<Faq> findAll();
}
