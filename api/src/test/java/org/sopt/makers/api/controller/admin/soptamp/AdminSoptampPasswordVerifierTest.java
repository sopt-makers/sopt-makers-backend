package org.sopt.makers.api.controller.admin.soptamp;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;

class AdminSoptampPasswordVerifierTest {

  @Test
  void 설정값과_같으면_통과한다() {
    assertThatCode(() -> new AdminSoptampPasswordVerifier("pw").verify("pw"))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"other"})
  void 설정값과_다르면_막는다(String password) {
    assertThatThrownBy(() -> new AdminSoptampPasswordVerifier("pw").verify(password))
        .isInstanceOf(SoptampException.class);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void 설정값이_비어_있으면_같은_값을_줘도_막는다(String adminPassword) {
    assertThatThrownBy(() -> new AdminSoptampPasswordVerifier(adminPassword).verify(adminPassword))
        .isInstanceOf(SoptampException.class);
  }
}
