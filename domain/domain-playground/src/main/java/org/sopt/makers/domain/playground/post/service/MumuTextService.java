package org.sopt.makers.domain.playground.post.service;

import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_MUMU_TEXT;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.OVERLAPPED_MUMU_TEXT_PERIOD;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.exception.PostException;
import org.sopt.makers.domain.playground.post.mumu.MumuText;
import org.sopt.makers.domain.playground.post.port.MumuTextRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MumuTextService {

  private final MumuTextRepositoryPort repositoryPort;
  private final Clock clock;

  public String getCurrentText() {
    LocalDateTime now = LocalDateTime.now(clock);
    return repositoryPort.findActiveAt(now).orElseGet(() -> resolveRepeatedText(now)).text();
  }

  public List<MumuText> findAll() {
    return repositoryPort.findAll().stream()
        .sorted(Comparator.comparing(MumuText::showStartDate).thenComparing(MumuText::id))
        .toList();
  }

  @Transactional
  public MumuText create(CreateMumuTextCommand command) {
    validateNoOverlap(null, command.showStartDate(), command.showEndDate());
    return repositoryPort.save(
        MumuText.create(
            command.text(), command.category(), command.showStartDate(), command.showEndDate()));
  }

  @Transactional
  public MumuText update(Long id, CreateMumuTextCommand command) {
    MumuText current = getById(id);
    validateNoOverlap(id, command.showStartDate(), command.showEndDate());
    return repositoryPort.save(
        current.update(
            command.text(), command.category(), command.showStartDate(), command.showEndDate()));
  }

  @Transactional
  public void delete(Long id) {
    repositoryPort.delete(getById(id));
  }

  private MumuText resolveRepeatedText(LocalDateTime now) {
    List<MumuText> texts = findAll();
    if (texts.isEmpty()) {
      throw new PostException(NOT_FOUND_MUMU_TEXT);
    }
    long days =
        ChronoUnit.DAYS.between(texts.getFirst().showStartDate().toLocalDate(), now.toLocalDate());
    return texts.get(Math.floorMod(days, texts.size()));
  }

  private MumuText getById(Long id) {
    return repositoryPort.findById(id).orElseThrow(() -> new PostException(NOT_FOUND_MUMU_TEXT));
  }

  private void validateNoOverlap(Long id, LocalDateTime startDate, LocalDateTime endDate) {
    if (!repositoryPort.findOverlapping(id, startDate, endDate).isEmpty()) {
      throw new PostException(OVERLAPPED_MUMU_TEXT_PERIOD);
    }
  }

  public record CreateMumuTextCommand(
      String text, String category, LocalDateTime showStartDate, LocalDateTime showEndDate) {}
}
