package org.sopt.makers.domain.app.home;

import static org.sopt.makers.domain.app.home.exception.HomeFailure.NOT_FOUND_USER_GENERATION;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import org.sopt.makers.domain.app.home.exception.HomeException;

public final class ActivityDurationCalculator {

  private static final int SOPT_START_YEAR = 2007;
  private static final int EVEN_GENERATION_START_MONTH = 3;
  private static final int ODD_GENERATION_START_MONTH = 9;

  private ActivityDurationCalculator() {}

  public static int calculate(List<Long> generations, LocalDate today) {
    if (generations == null || generations.isEmpty()) {
      throw new HomeException(NOT_FOUND_USER_GENERATION);
    }
    Long firstGeneration = generations.stream().min(Comparator.naturalOrder()).orElseThrow();
    Period period = Period.between(getGenerationStartDate(firstGeneration), today);
    return period.getYears() * 12 + period.getMonths() + 1;
  }

  private static LocalDate getGenerationStartDate(Long generation) {
    int startMonth = generation % 2 == 0 ? EVEN_GENERATION_START_MONTH : ODD_GENERATION_START_MONTH;
    int startYear = SOPT_START_YEAR + (int) (generation / 2);
    return LocalDate.of(startYear, startMonth, 1);
  }
}
