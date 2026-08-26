package org.sopt.makers.domain.app.operationconfig.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.operationconfig.OperationConfigCategory.PLAYGROUND_POST;
import static org.sopt.makers.domain.app.operationconfig.OperationConfigCategory.REVIEW_FORM;
import static org.sopt.makers.domain.app.operationconfig.OperationConfigCategory.SOPTAMP_BATCH;
import static org.sopt.makers.domain.app.operationconfig.exception.OperationConfigFailure.NOT_FOUND_OPERATION_CONFIG;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.exception.OperationConfigException;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;

@DisplayName("OperationConfigService 테스트")
class OperationConfigServiceTest {

  private InMemoryOperationConfigPort operationConfigPort;
  private OperationConfigService operationConfigService;

  @BeforeEach
  void setUp() {
    operationConfigPort = new InMemoryOperationConfigPort();
    operationConfigService = new OperationConfigService(operationConfigPort);
  }

  @Test
  @DisplayName("카테고리와 key로 value를 조회한다")
  void getsValueByCategoryAndKey() {
    operationConfigPort.save(
        OperationConfig.text(
            REVIEW_FORM, "linkUrl", "https://example.com/sopt-letter-report", "솝레터 익명 신고 폼 URL"));

    assertThat(operationConfigService.getValue(REVIEW_FORM, "linkUrl"))
        .isEqualTo("https://example.com/sopt-letter-report");
  }

  @Test
  @DisplayName("운영 설정이 없으면 NOT_FOUND_OPERATION_CONFIG 예외가 발생한다")
  void throwsWhenConfigMissing() {
    assertThatThrownBy(() -> operationConfigService.getValue(REVIEW_FORM, "linkUrl"))
        .isInstanceOf(OperationConfigException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_OPERATION_CONFIG);
  }

  @Test
  @DisplayName("findValue는 설정이 없어도 예외 없이 빈 값을 준다")
  void findValueReturnsEmptyWhenMissing() {
    assertThat(operationConfigService.findValue(REVIEW_FORM, "linkUrl")).isEmpty();
  }

  @Test
  @DisplayName("카테고리 안의 설정을 key -> value 맵으로 읽는다")
  void readsCategoryAsValueMap() {
    operationConfigPort.save(OperationConfig.text(PLAYGROUND_POST, "자유.imageUrl", "free.png", ""));
    operationConfigPort.save(
        OperationConfig.text(PLAYGROUND_POST, "unknown.imageUrl", "unknown.png", ""));
    operationConfigPort.save(OperationConfig.text(REVIEW_FORM, "linkUrl", "https://form", ""));

    assertThat(operationConfigService.getValuesByCategory(PLAYGROUND_POST))
        .containsExactlyInAnyOrderEntriesOf(
            Map.of("자유.imageUrl", "free.png", "unknown.imageUrl", "unknown.png"));
  }

  @Test
  @DisplayName("설정이 없으면 새로 저장한다")
  void upsertInsertsWhenMissing() {
    operationConfigService.upsertValue(SOPTAMP_BATCH, "UPSERT_CRON", "0 0 3 * * *", "배치 cron");

    assertThat(operationConfigService.getValue(SOPTAMP_BATCH, "UPSERT_CRON"))
        .isEqualTo("0 0 3 * * *");
  }

  @Test
  @DisplayName("이미 있는 설정은 새 행을 만들지 않고 값만 갱신한다")
  void upsertUpdatesExistingRow() {
    operationConfigService.upsertValue(SOPTAMP_BATCH, "UPSERT_CRON", "0 0 3 * * *", "배치 cron");
    Long savedId =
        operationConfigPort.findByCategoryAndKey(SOPTAMP_BATCH, "UPSERT_CRON").orElseThrow().id();

    operationConfigService.upsertValue(SOPTAMP_BATCH, "UPSERT_CRON", "0 0 5 * * *", "배치 cron");

    assertThat(operationConfigPort.findAllByCategory(SOPTAMP_BATCH)).hasSize(1);
    assertThat(operationConfigPort.findByCategoryAndKey(SOPTAMP_BATCH, "UPSERT_CRON"))
        .get()
        .satisfies(
            config -> {
              assertThat(config.id()).isEqualTo(savedId);
              assertThat(config.value()).isEqualTo("0 0 5 * * *");
            });
  }

  private static final class InMemoryOperationConfigPort implements OperationConfigPort {

    private final Map<Long, OperationConfig> store = new LinkedHashMap<>();
    private long sequence = 1L;

    @Override
    public List<OperationConfig> findAllByCategory(OperationConfigCategory category) {
      return store.values().stream().filter(config -> config.category() == category).toList();
    }

    @Override
    public Optional<OperationConfig> findByCategoryAndKey(
        OperationConfigCategory category, String key) {
      return findAllByCategory(category).stream()
          .filter(config -> config.key().equals(key))
          .findFirst();
    }

    @Override
    public OperationConfig save(OperationConfig operationConfig) {
      Long id = operationConfig.id() == null ? sequence++ : operationConfig.id();
      OperationConfig saved =
          new OperationConfig(
              id,
              operationConfig.key(),
              operationConfig.value(),
              operationConfig.type(),
              operationConfig.category(),
              operationConfig.description());
      store.put(id, saved);
      return saved;
    }
  }
}
