package org.sopt.makers.api.common.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.auth.exception.AuthFailure.MISSING_AUTHORIZATION_HEADER;

import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

@DisplayName("CurrentUserIdArgumentResolver 테스트")
class CurrentUserIdArgumentResolverTest {

  private final CurrentUserIdArgumentResolver resolver = new CurrentUserIdArgumentResolver();
  private final NativeWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("인증이 있으면 principal 을 Long 으로 준다")
  void resolvesUserIdWhenAuthenticated() {
    SecurityContextHolder.getContext().setAuthentication(new CustomAuthentication("7", null));

    Object resolved = resolver.resolveArgument(requiredParameter(), null, webRequest, null);

    assertThat(resolved).isEqualTo(7L);
  }

  @Test
  @DisplayName("인증이 없고 required 가 true 면 MISSING_AUTHORIZATION_HEADER 예외가 발생한다")
  void throwsWhenRequiredAndUnauthenticated() {
    assertThatThrownBy(() -> resolver.resolveArgument(requiredParameter(), null, webRequest, null))
        .isInstanceOf(AuthException.class)
        .extracting("error")
        .isEqualTo(MISSING_AUTHORIZATION_HEADER);
  }

  @Test
  @DisplayName("인증이 없고 required 가 false 면 null 을 준다")
  void resolvesNullWhenOptionalAndUnauthenticated() {
    Object resolved = resolver.resolveArgument(optionalParameter(), null, webRequest, null);

    assertThat(resolved).isNull();
  }

  private static MethodParameter requiredParameter() {
    return parameterOf("required");
  }

  private static MethodParameter optionalParameter() {
    return parameterOf("optional");
  }

  private static MethodParameter parameterOf(String methodName) {
    try {
      Method method = Sample.class.getDeclaredMethod(methodName, Long.class);
      return new MethodParameter(method, 0);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unused")
  private static final class Sample {
    void required(@CurrentUserId Long userId) {}

    void optional(@CurrentUserId(required = false) Long userId) {}
  }
}
