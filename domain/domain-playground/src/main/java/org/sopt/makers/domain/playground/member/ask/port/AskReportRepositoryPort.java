package org.sopt.makers.domain.playground.member.ask.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.AskReport;

public interface AskReportRepositoryPort {

  AskReport save(AskReport askReport);

  Optional<AskReport> findById(Long reportId);

  void deleteById(Long reportId);
}
