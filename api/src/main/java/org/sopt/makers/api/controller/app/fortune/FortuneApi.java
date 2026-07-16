package org.sopt.makers.api.controller.app.fortune;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneCardResponse;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "운세", description = "앱 운세(솝마디/운세카드) API")
public interface FortuneApi {

  @Operation(summary = "오늘의 솝마디 조회")
  ResponseEntity<FortuneResponse> getTodayFortuneWord(
      @Parameter(hidden = true) Long userId, LocalDate todayDate);

  @Operation(summary = "오늘의 운세카드 조회")
  ResponseEntity<FortuneCardResponse> getTodayFortuneCard(@Parameter(hidden = true) Long userId);
}
